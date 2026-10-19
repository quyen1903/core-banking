# Auth module: làm gì với từng bảng?

Đây là checklist triển khai **server-side session** bằng Spring Security,
Spring Session JDBC và PostgreSQL. Tài liệu mô tả việc cần làm, chưa xác nhận
rằng migration, entity, API hoặc kiểm soát bảo mật đã hoàn thành. Chỉ đánh dấu
một việc khi đã có code và kết quả kiểm tra tương ứng.

- [DDL và thiết kế database chi tiết](auth-session-schema-proposal.md).
- [Ranh giới và API mục tiêu của identity module](identity-module-design.md).
- [Quay lại README](../../README.md).

## Nhớ nhanh

| Bảng | Trả lời câu hỏi gì? | Phần mình cần triển khai |
| --- | --- | --- |
| `identity_user` | Ai đăng nhập? Thuộc customer/staff nào? Có được hoạt động không? | Entity, domain, migration, port/adapter và use case |
| `identity_credential` | Kiểm tra mật khẩu thế nào? Có bắt buộc đổi mật khẩu không? | Entity, encoder adapter, use case đặt/đổi/reset mật khẩu |
| `identity_role` | Có những vai trò nào? | Entity, catalog vai trò, migration/seed và quy tắc sử dụng |
| `identity_permission` | Có những hành động nào được cấp quyền? | Entity, catalog quyền và điểm kiểm tra trong application |
| `identity_user_role` | User được gán vai trò nào? | Entity liên kết, ràng buộc, use case gán/thu hồi và audit |
| `identity_role_permission` | Vai trò được thực hiện những hành động nào? | Entity liên kết, ma trận quyền và xử lý thay đổi quyền |
| `spring_session` | Phiên đăng nhập nào còn hiệu lực? | Migration và cấu hình framework; không cần tự viết JPA entity |
| `spring_session_attributes` | Phiên đó giữ thông tin xác thực tối thiểu nào? | Dùng cơ chế lưu/đọc của Spring Session; không mở CRUD công khai |
| `audit_event` | Ai đã làm gì, lúc nào, kết quả ra sao? | Capability audit dùng chung, port/adapter, lưu bền vững và quyền DB |

Một bảng không đồng nghĩa với một controller hoặc một API CRUD. API biểu diễn
hành động nghiệp vụ, chẳng hạn `DisableIdentityUseCase`, thay vì cho phép client
cập nhật tùy ý các cột trạng thái, role hoặc credential.

## Quy tắc chung trước khi viết entity

- [ ] Đối chiếu migration history và schema hiện có, nhất là bảng `appuser`.
  Checkout hiện chưa có SQL migration trong `src/main/resources/db/migration`,
  trong khi test cũ có tham chiếu lịch sử migration. Xác minh lịch sử trước khi
  chọn version mới; không tự tạo lại `V1` hoặc sửa migration đã áp dụng.
- [ ] Dùng DDL trong bản đề xuất làm điểm bắt đầu để review, không chạy trực tiếp
  vào database đang có dữ liệu khi chưa có kế hoạch chuyển đổi.
- [ ] Theo thiết kế identity hiện có: `Long id` nội bộ, `UUID publicId` trên API.
  Các FK nội bộ auth dùng `id`; owner dùng UUID do module chủ quản cung cấp.
- [ ] Các bảng nghiệp vụ mutable có `created_at`, `updated_at`, `version`;
  timestamp ánh xạ `Instant` / `timestamptz`, version ánh xạ `@Version`.
  Session theo hợp đồng schema của framework, audit theo mô hình bản ghi bất biến.
- [ ] Migration khai báo `NOT NULL`, PK, FK, unique, check và index cần thiết.
  `@NotNull` và annotation entity không thay thế ràng buộc trong migration.
- [ ] Viết domain method cho chuyển trạng thái; application gọi outbound port;
  persistence adapter mới dùng Spring Data repository. HTTP trả DTO/snapshot.
- [ ] Mọi use case nhạy cảm kiểm tra actor, quyền và phạm vi tài nguyên; đặt
  transaction ở application và ghi audit cho thay đổi bắt buộc có bằng chứng.

## identity_user

**Vai trò:** danh tính đăng nhập và liên kết với chủ sở hữu nghiệp vụ.

Các trường theo bản đề xuất: `id`, `public_id`, `username`,
`username_normalized`, `owner_type`, `owner_id`, `status`, `security_version`,
`created_at`, `updated_at`, `version`.

- [ ] Sau khi chốt migration/chuyển dữ liệu, sửa mapping của
  [IdentityUserJpaEntity](../../src/main/java/com/quinnbank/core/auth/adapter/out/persistent/identity_user/IdentityUserJpaEntity.java)
  từ `appuser` sang `identity_user`; thêm ID nội bộ và public UUID theo thiết kế.
- [ ] Tạo migration với unique `public_id`, unique `username_normalized`,
  index `(owner_type, owner_id)` và check status/owner type.
- [ ] Đăng ký và đăng nhập dùng cùng quy tắc chuẩn hóa username. Bản DDL đề xuất
  bộ ký tự ASCII và không phân biệt hoa thường; cần chốt quy tắc trước khi code.
- [ ] `owner_type` là `CUSTOMER` hoặc `STAFF`; xác minh owner qua application
  contract của CIF/staff. Không gọi thẳng repository của module khác.
- [ ] Chốt các chuyển trạng thái `PENDING_ACTIVATION`, `ACTIVE`, `DISABLED` và
  điều kiện kích hoạt; chưa đủ credential/MFA bắt buộc thì chưa cấp quyền đầy đủ.
- [ ] Tạo use case provision, đọc snapshot, kích hoạt và vô hiệu hóa danh tính.
  Input không cho client tự chọn quyền quản trị hoặc tự thay đổi security version.
- [ ] Phân biệt `version` để chống ghi đè đồng thời với `security_version` để
  phát hiện session cũ. Việc vô hiệu hóa phải cập nhật version bảo mật và audit
  trong cùng transaction.
- [ ] Chuyển password và yêu cầu đổi mật khẩu sang credential. Chỉ loại bỏ các
  cờ khóa/hết hạn cũ khi đã xác định quy tắc thay thế và kế hoạch giữ dữ liệu.
- [ ] Test username trùng sau chuẩn hóa, owner không hợp lệ, chuyển trạng thái
  sai, truy cập sai scope, xung đột cập nhật và session của user bị vô hiệu hóa.

**Cần chốt:** một owner được có bao nhiêu identity. Một identity có một owner
không có nghĩa là mặc định một owner chỉ có một identity. `owner_type + owner_id`
là liên kết logic; SQL hiện chưa chứng minh owner tồn tại trong CIF/staff.

## identity_credential

**Vai trò:** lưu một password credential hiện tại cho mỗi identity.

Các trường: `user_id` vừa là PK vừa là FK, `password_hash`,
`password_changed_at`, `must_change_password`, timestamps và `version`.

- [ ] Hoàn thiện entity persistence. Có thể đặt tên `IdentityCredentialJpaEntity`
  để phân biệt với domain model; không dùng entity làm request/response DTO.
- [ ] Tạo FK tới `identity_user.id`, bảo đảm mỗi user tối đa một credential
  trong mô hình password hiện tại. Pending user có thể chưa có credential.
- [ ] Dùng password encoder đã được phê duyệt qua adapter; lưu toàn bộ encoded
  hash. Kiểm tra chiều dài cột đủ cho encoding thực tế, không tự viết hash/salt.
- [ ] Viết use case đặt mật khẩu ban đầu, đổi mật khẩu và reset mật khẩu.
  Phân biệt đổi mật khẩu bởi chính user với reset có quyền của actor khác.
- [ ] Khi đổi/reset: cập nhật hash, thời điểm, yêu cầu đổi mật khẩu,
  `identity_user.security_version` và audit trong transaction được kiểm soát.
- [ ] Khi `must_change_password=true`, chỉ cho phép các bước cần thiết để đổi
  mật khẩu/hoàn tất xác thực; chưa mở toàn bộ API nghiệp vụ.
- [ ] Password/hash không đi vào DTO public, log, session principal hoặc payload
  audit. Domain không phụ thuộc Spring Security hay JPA.
- [ ] Test credential thiếu, mật khẩu sai, đổi/reset sai quyền, session cũ bị từ
  chối và rollback khi ghi credential/audit thất bại.

Reset token dùng một lần, thời hạn token và MFA recovery cần thiết kế riêng;
không đưa token thô hoặc MFA secret vào bảng password credential này.

## identity_role

**Vai trò:** catalog các vai trò, ban đầu theo thiết kế là `CUSTOMER`, `TELLER`,
`ADMIN`. Tạo role trong catalog chưa gán role đó cho bất kỳ user nào.

- [ ] Tạo entity và migration: ID, `code` unique, mô tả, `enabled`, timestamps,
  `version`; map code bằng tên rõ ràng, không dùng ordinal của enum.
- [ ] Seed catalog bằng migration được review; không seed user, password mặc
  định hoặc tự động gán `ADMIN`.
- [ ] Chốt actor được quản lý catalog và quy trình bootstrap operator đầu tiên
  có kiểm soát, audit và xác thực bắt buộc; không mở public endpoint cấp admin.
- [ ] Bộ đọc quyền bỏ qua role bị disable. Thay đổi role phải xử lý quyền của
  các user đang giữ role đó, kể cả khi session của họ đã tồn tại.
- [ ] Test code trùng, role không tồn tại/bị disable và thay đổi không đủ quyền.

## identity_permission

**Vai trò:** catalog hành động mà application thực sự kiểm tra.

- [ ] Tạo entity và migration: ID, `code` unique, mô tả, `enabled`, timestamps,
  `version`.
- [ ] Định nghĩa code theo hành động use case; ví dụ code đề xuất
  `IDENTITY_USER_READ`, `IDENTITY_USER_DISABLE`, `IDENTITY_ROLE_ASSIGN`.
  Đây là ví dụ để review, chưa phải ma trận quyền đã được phê duyệt.
- [ ] Liên kết mỗi code với điểm kiểm tra authorization rõ ràng trong application.
  Có quyền đọc user vẫn phải kiểm tra user đó có nằm trong scope cho phép không.
- [ ] Bộ đọc quyền bỏ qua permission bị disable; bảo vệ và audit thay đổi catalog.
- [ ] Test thiếu quyền, sai scope và permission bị disable. Có cả đường cho phép
  và đường bị từ chối.

## identity_user_role

**Vai trò:** gán role cho identity, mỗi cặp `(user_id, role_id)` xuất hiện tối đa
một lần.

- [ ] Tạo entity liên kết với composite key (`@EmbeddedId` hoặc `@IdClass`),
  timestamps và `@Version`; PK ghép đồng thời chặn cặp trùng.
- [ ] Tạo hai FK với xóa bị hạn chế, và index `role_id` để tìm user theo role.
- [ ] Viết use case gán/thu hồi role, kiểm tra actor, owner type và scope.
  Identity customer không được nhận role dành cho staff/admin qua input client.
- [ ] Điều khiển thay đổi qua aggregate identity; dùng concurrency control trên
  identity khi cần bảo vệ các quy tắc liên quan nhiều assignment. Version của
  từng dòng liên kết riêng lẻ chưa đủ bảo vệ mọi quy tắc giữa nhiều dòng.
- [ ] Gán/thu hồi, cập nhật `security_version` và ghi audit cùng transaction.
  Chốt kết quả khi client retry gán/thu hồi cùng một role.
- [ ] Test FK sai, cặp trùng, retry, hai lệnh đồng thời, tự nâng quyền và session
  đang tồn tại sau khi bị thu hồi quyền.

Không dùng cascade hoặc mapper ghi rộng để bỏ qua các use case quản lý role.

## identity_role_permission

**Vai trò:** xác định role được cấp những permission nào.

- [ ] Tạo entity liên kết, PK `(role_id, permission_id)`, hai FK, timestamps,
  `version` và index `permission_id`.
- [ ] Chốt ma trận role/permission rõ ràng trước khi seed liên kết; không mặc
  định mọi role hoặc mọi staff được toàn bộ quyền.
- [ ] Viết use case chỉnh ma trận có authorization, concurrency control theo
  role và audit cho thao tác cấp/thu hồi.
- [ ] Chọn cách phản ánh thay đổi tới user đang đăng nhập: vô hiệu hóa tất cả
  identity bị ảnh hưởng, hoặc kiểm tra quyền hiện hành tại mỗi use case được bảo
  vệ. Chỉ tăng version của một user không xử lý được thay đổi trên cả role.
- [ ] Test cặp trùng, FK sai, actor không có quyền, chỉnh đồng thời và thu hồi
  permission có hiệu lực với session trên nhiều instance.

## spring_session

**Vai trò:** kho phiên đăng nhập được Spring Session JDBC quản lý.

- [ ] Thêm dependency Boot-managed cho Spring Security và Spring Session JDBC
  theo quy trình `LIBRARY.md`; xác minh phiên bản thực tế đã resolve.
- [ ] Lấy script PostgreSQL từ đúng artifact Spring Session đó, đưa vào migration
  Flyway mới. Giữ kiểu dữ liệu/index theo framework; không tự chuyển tất cả cột
  thời gian session sang `timestamptz` hoặc ID session sang kiểu UUID của nghiệp vụ.
- [ ] Đặt `spring.session.jdbc.initialize-schema=never` để Flyway quản lý schema.
- [ ] Cấu hình principal/index mapping bằng định danh identity ổn định; session
  chỉ giữ dữ liệu tối thiểu, mức xác thực và security version tại lúc xác thực.
- [ ] Tích hợp login/logout bằng cơ chế Spring Security; bảo đảm context được
  lưu đúng và session ID được đổi sau xác thực. Không tự tạo token format.
- [ ] Kiểm tra status/security version hiện hành trước truy cập được bảo vệ,
  cùng authorization tại application. Xóa session vật lý chỉ là một phần của
  cơ chế thu hồi; không dựa vào snapshot quyền cũ vô thời hạn.
- [ ] Cấu hình HTTPS, cookie `Secure`, `HttpOnly`, `SameSite` phù hợp, CSRF,
  idle timeout và kiểm tra absolute timeout phía server theo chính sách.
- [ ] Ghi rõ lịch và tải DB của job dọn session hết hạn. DB session lỗi phải
  trả lỗi an toàn, không cấp quyền truy cập vào endpoint được bảo vệ.
- [ ] Test session ID rotation, thiếu/sai CSRF, hết hạn, logout, thu hồi sau đổi
  credential/quyền, nhiều instance và lỗi kho session.

## spring_session_attributes

**Vai trò:** lưu thuộc tính phiên theo cơ chế của Spring Session.

- [ ] Dùng schema framework, giữ PK ghép, FK tới session và hành vi xóa thuộc
  tính khi session bị xóa. Không cần tự viết JPA entity/repository cho bảng này.
- [ ] Chỉ lưu principal/context tối thiểu; không lưu password/hash hoặc nguyên
  JPA aggregate, thông tin tài chính hay dữ liệu khách hàng không cần thiết.
- [ ] Kiểm soát kiểu dữ liệu được serialize; không bật unsafe polymorphic
  deserialization hoặc nhận dữ liệu serialized tùy ý từ client.
- [ ] Kiểm tra round-trip context giữa các instance và tương thích khi deploy
  phiên bản ứng dụng mới; bảo vệ quyền đọc/ghi bảng và backup theo phân loại secret.

## audit_event

**Vai trò:** bằng chứng dùng chung về hành động nhạy cảm, không thay bằng log
chẩn đoán. Có thể dùng adapter JDBC/JPA phù hợp; không bắt buộc mọi module có
một bản sao audit entity/repository.

- [ ] Tạo capability audit và `AuditEventPort` hẹp; module auth ghi qua port,
  không gọi repository riêng của module khác.
- [ ] Tạo migration theo DDL đề xuất: event ID, thời điểm UTC, actor, action,
  target, outcome, reason, correlation ID và metadata an toàn theo nhu cầu.
- [ ] Mỗi loại event có schema/allowlist cho `details`; không dump request,
  user entity, credential, token, session ID hoặc MFA secret.
- [ ] Thay đổi identity/credential/role và audit local commit cùng transaction;
  audit lỗi thì thay đổi đó không được báo thành công.
- [ ] Bằng chứng login thất bại cần boundary lưu riêng để không mất theo rollback
  của lần xác thực bị từ chối. Ghi audit bắt buộc lỗi phải có failure mode rõ ràng.
- [ ] Runtime writer chỉ có quyền insert audit, không sở hữu bảng hoặc có quyền
  update/delete/truncate/DDL. Quyền đọc và retention dùng danh tính được kiểm soát.
- [ ] Không cascade xóa audit khi đổi/xóa dữ liệu nguồn. Quy định retention và
  bảo vệ bằng chứng mạnh hơn cần thiết kế storage được phê duyệt; quyền insert-only
  không tự ngăn được quản trị viên database sửa dữ liệu.
- [ ] Test rollback khi audit lỗi, lưu bằng chứng login thất bại, redaction,
  quyền DB và actor/target scope của sự kiện.

## Thứ tự làm để không bị rối

1. **Chốt schema và lịch sử migration.** Review ID, username, owner linkage,
   trạng thái, ma trận quyền, audit contract và dữ liệu `appuser` nếu có.
2. **Làm identity foundation.** Migration và mapping cho user/credential; domain,
   port/adapter, use case, audit và test liên quan trước khi mở API thay đổi dữ liệu.
3. **Làm RBAC.** Catalog role/permission, hai bảng liên kết, quản lý gán/thu hồi,
   kiểm tra scope và phản ánh thay đổi quyền cho session.
4. **Làm login/session.** Thêm schema framework và cấu hình Spring Security,
   MFA bắt buộc, CSRF, timeout, revocation, abuse control và login/logout/me.
5. **Kiểm chứng tích hợp.** Migration trên DB synthetic cô lập, mapping, transaction,
   concurrency, quyền DB, lỗi audit/session và truy cập chéo customer/staff.

Mỗi bước đi trọn từ migration đến use case và test có ý nghĩa. Không mở endpoint
nhạy cảm chỉ vì đã tạo xong entity hoặc bảng.

## Việc tiếp theo với file đang mở

Với `IdentityUserJpaEntity`, thứ tự hợp lý là:

1. Chốt migration/chuyển dữ liệu trước khi đổi `@Table` và kiểu ID.
2. Giữ dữ liệu identity trong user; đưa password và yêu cầu đổi mật khẩu sang
   credential, với quy tắc chuyển dữ liệu rõ ràng cho các cờ hiện có.
3. Thêm owner linkage, status, username chuẩn hóa, hai loại version và timestamps
   theo thiết kế; `createdAt` hiện còn là `LocalDate` cần được xử lý.
4. Hoàn thiện hai entity và persistence mapping đầu tiên, rồi test với migration
   trên DB cô lập; sau đó nối domain/application qua port và audit.

## Những việc ngoài các bảng trên vẫn cần làm

- MFA enrollment/challenge, step-up và recovery; user chưa hoàn tất MFA bắt buộc
  chỉ có quyền hạn chế. Chọn storage/provider theo thiết kế riêng.
- Theo dõi login thất bại, khóa tạm và rate limit trong kho trạng thái dùng chung
  giữa các instance; có thể thêm `identity_login_state` khi chốt cơ chế này.
- Activation/reset token: vòng đời, thời hạn, chống replay và dùng một lần.
- Idempotency/retry của lệnh provision/gán quyền, cùng cơ chế bootstrap operator
  có kiểm soát. Không tự đặt credential mặc định để giải quyết bootstrap.
- Xác minh owner còn hợp lệ và quyền trên từng tài nguyên trong application;
  có role không đồng nghĩa được đọc mọi customer hoặc thực hiện mọi giao dịch.

Các chính sách chưa có giá trị cụ thể (timeout, ngưỡng khóa, số identity mỗi owner,
retention, ma trận quyền) phải được chốt theo repository/bank policy trước khi
phần triển khai phụ thuộc vào chúng được phát hành.

## Trạng thái kiểm chứng

Checklist này là hướng dẫn tài liệu. Nó chưa triển khai kiểm soát bảo mật,
migration hoặc logic tài chính. DDL, migration history, dữ liệu legacy và runtime
authentication cần được xác minh trong từng bước triển khai. Các ô chưa đánh dấu
không đại diện cho kết luận rằng code tương ứng đã hoàn thành.

Nguồn đối chiếu: [SECURITY.md](../../SECURITY.md),
[LIBRARY.md](../../LIBRARY.md), [CODING_STANDARD.md](../../CODING_STANDARD.md),
[kiến trúc](../ARCHITECTURE.md) và [DDL đề xuất](auth-session-schema-proposal.md).
