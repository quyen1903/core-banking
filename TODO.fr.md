# QuinnBank Core — Liste des tâches

[English](TODO.md)

Mise à jour : 13 septembre 2026. Ce backlog repose sur le code source et la documentation existants. L’application et la suite de tests n’ont pas été exécutées lors de sa préparation. Les tâches de vérification ci-dessous ne signifient pas que des défauts ont été reproduits.

Cette modification documentaire relève de R0. Le niveau de risque de chaque tâche décrit le risque de son implémentation selon [AGENTS.md](AGENTS.md). Ne cocher une tâche que lorsque des preuves satisfont ses critères d’acceptation.

## État initial

| Domaine | Éléments présents dans le dépôt | Prochaines étapes |
| --- | --- | --- |
| CIF, employee, identity | Modèles, persistance, cas d’utilisation et certains tests unitaires | Compléter les parcours, les périmètres d’autorisation et les tests d’intégration |
| Account | Ouverture, requêtes, idempotence et projection des soldes | Contrôle du titulaire, cycle de vie du compte et vérification en base |
| Ledger | Journaux équilibrés, comptabilisation, gestion des répétitions/conflits et appels de projection des soldes | Tests d’atomicité et de concurrence, contrepassation et rapprochement |
| Security | Sécurité des méthodes, permissions sur les API ; HTTP Basic et utilisateur d’amorçage en `dev`/`test` | Authentification client et configuration de sécurité des environnements de déploiement |
| Customer web | Écrans bancaires, client API et données synthétiques | Authentification, tableau de bord, profil, transactions et virements utilisent encore des simulations ou fonctions provisoires |
| Base de données / build | Flyway V1–V6, Gradle, configurations d’environnement et scripts frontend | Valider les migrations, la reproductibilité des builds/tests et les preuves de livraison |

Sources principales : [architecture](docs/ARCHITECTURE.md), [account](docs/architecture/account-module-design.md), [identity](docs/architecture/identity-module-design.md), [environnements](docs/operations/environments.md), [frontend](customer-web/README.md), [configuration de sécurité](src/main/java/com/quinnbank/core/security/SecurityConfiguration.java), [service ledger](src/main/java/com/quinnbank/core/ledger/application/service/PostLedgerJournalService.java).

## P0 — Fondations requises avant d’activer les parcours bancaires réels

- [ ] **T01 · Établir l’état de référence des builds/tests — R1.** Exécuter les tests backend sur une base PostgreSQL locale réservée aux tests, avec des données synthétiques ; lancer lint, typecheck et build du frontend. Terminé lorsque les commandes, versions des outils, résultats et échecs restants sont consignés ; ne jamais ignorer des tests en échec pour annoncer un succès.
- [ ] **T02 · Documenter l’installation locale — R0.** Compléter le README racine avec la chaîne Java de `build.gradle`, le wrapper Gradle, PostgreSQL, pnpm selon `package.json`, les variables d’environnement, les migrations et le démarrage des deux applications. Terminé lorsqu’une nouvelle personne peut suivre ces instructions depuis une copie propre du dépôt avec une configuration locale.
- [ ] **T03 · Concevoir et implémenter l’authentification — R3.** S’appuyer sur les fondations identity pour réaliser la connexion, le renouvellement, la déconnexion et la gestion des identités verrouillées/désactivées. Définir sessions/jetons, expiration, rotation/révocation et CSRF/CORS selon le mécanisme retenu ; ne pas inventer de format de jeton ni stocker de jetons dans localStorage. Terminé lorsque les tests couvrent l’accès valide, le mot de passe incorrect, l’expiration, la révocation et le rejeu des jetons de renouvellement, le cas échéant.
- [ ] **T04 · Appliquer les autorisations par ressource — R3.** Ajouter des politiques dans les flux applicatifs de CIF, account, identity et employee : acteur, titulaire, rôle et agence/périmètre métier. Terminé lorsque le client A ne peut ni lire ni modifier les ressources du client B, que les employés hors périmètre sont refusés et que les cas d’utilisation appelés sans HTTP appliquent aussi les autorisations.
- [ ] **T05 · Configurer la sécurité par environnement — R3.** Vérifier le comportement hors `dev`/`test`, supprimer la dépendance à des valeurs par défaut non conçues explicitement et aligner les permissions d’amorçage sur les permissions actuelles. Terminé lorsque les tests couvrent les endpoints publics/protégés, la disponibilité de l’amorçage uniquement dans les environnements autorisés, des réponses 401/403 sûres et l’absence de données internes dans les réponses actuator.
- [ ] **T06 · Auditer les actions sensibles — R3.** Concevoir le stockage d’audit et ajouter des événements pour identity/role/credential, CIF et account : acteur, action, périmètre de ressource, décision, motif, horodatage UTC et identifiant de corrélation. Terminé lorsque les tests vérifient la protection contre les modifications/suppressions non autorisées, le masquage des données sensibles et le comportement en cas d’échec d’écriture de l’audit ; les événements de domaine ou logs applicatifs seuls ne constituent pas une preuve suffisante.
- [ ] **T07 · Prouver l’atomicité des transactions du ledger — R4.** Ajouter des tests d’intégration PostgreSQL pour la comptabilisation et la projection des soldes dans une même transaction. Terminé lorsqu’un échec de mise à jour d’un compte ou de persistance annule à la fois le journal et toutes les modifications de solde, sans comptabilisation partielle.
- [ ] **T08 · Prouver l’idempotence et le comportement concurrent — R4.** Tester l’ouverture de compte, la comptabilisation et la projection avec des requêtes simultanées, des reprises après expiration du délai, une même clé avec des contenus différents, plusieurs acteurs partageant une clé, des conflits de verrouillage et des fonds insuffisants. Terminé lorsqu’il n’y a qu’un seul effet financier, que le périmètre des clés est explicite, que les reprises valides renvoient des résultats cohérents et que les conflits ont des codes d’erreur stables.
- [ ] **T09 · Valider les migrations et les mappings — R4.** Exécuter V1–V6 sur une nouvelle base et vérifier les chemins de mise à niveau avec des données synthétiques ; comparer clés étrangères, contraintes d’unicité/index, précision/échelle, nullabilité, champs d’audit et versions. Terminé lorsque les validations Flyway et JPA réussissent et que les risques de reprise de données, verrouillage et déploiement sont évalués ; corriger par de nouvelles migrations si les précédentes ont atteint un environnement partagé.

## P1 — Compléter les parcours déjà représentés dans l’interface

- [ ] **T10 · Connecter l’authentification du frontend — R3 ; après T03–T05.** Remplacer `DEMO_SESSION` dans `customer-web/api/auth.ts` par l’authentification backend. Terminé lorsque connexion/déconnexion/expiration fonctionnent, que le cache privé est vidé lors d’un changement d’utilisateur et que le mode démo est clairement identifiable.
- [ ] **T11 · API de liste des comptes et de tableau de bord — R3 ; après T04.** Fournir des requêtes limitées aux comptes du périmètre de l’acteur, une pagination si nécessaire et des synthèses de soldes par devise. Terminé lorsque les simulations de `accounts.ts`/`dashboard.ts` sont remplacées, que les tests d’accès entre clients réussissent et que plusieurs devises ne sont pas additionnées en un solde unique sans conversion.
- [ ] **T12 · Harmoniser les parcours profil et CIF — R3 ; après T04, T06.** Identifier le flux CIF actif entre les packages `adapter`/`infrastructure` et les contrats/modèles aux noms similaires avant de les normaliser. Relier les requêtes de profil au bon acteur ; n’autoriser que les modifications des champs permis. Terminé lorsque les contrats sont explicites, que les données personnelles sont minimisées et que les tests de validation et de protection contre l’affectation massive passent.
- [ ] **T13 · Produits et cycle de vie des comptes — R4 ; après T04, T06–T09.** Définir les règles produit, l’éligibilité, les comportements de blocage/déblocage/clôture et les droits d’approbation ; implémenter les transitions dans le domaine. Terminé lorsque les tests couvrent les soldes/obligations restants selon la politique convenue, l’idempotence, les conflits de version, l’audit et les transitions interdites.
- [ ] **T14 · Concevoir les virements internes — R4 ; après T07–T09.** Définir le module responsable, l’acteur, les comptes source/destination, les plafonds, la devise, les frais éventuels, les dates de valeur/comptabilisation, les états et les étapes d’authentification/approbation. Terminé lorsqu’une conception revue, un contrat API et des parcours d’échec/reprise/contrepassation existent ; ne pas inventer de plafonds métier non décidés.
- [ ] **T15 · Implémenter les virements via le ledger — R4 ; après T03–T09, T14.** Ajouter un cas d’utilisation et un endpoint de virement appelant le ledger via un contrat applicatif. Terminé lorsque les tests couvrent les autorisations, les fonds insuffisants, les comptes invalides, les devises incompatibles, les doublons, les débits concurrents, les délais dépassés et le rollback ; aucune modification directe du solde hors du flux ledger/projection conçu.
- [ ] **T16 · Connecter l’écran de virement — R4 ; après T10, T15.** Remplacer la fonction provisoire dans `customer-web/api/transfers.ts` ; conserver la clé d’idempotence lors de la reprise d’une même demande et interroger le statut lorsque le résultat est incertain. Terminé lorsque l’interface reflète l’état backend sans traiter un délai dépassé comme un échec définitif ni annoncer elle-même la réussite du virement.
- [ ] **T17 · Historique des transactions — R3 ; après T04 et le contrat ledger/transfer.** Ajouter des requêtes avec périmètre du titulaire, pagination et filtres de date/statut ; connecter `customer-web/api/transactions.ts` et l’historique des virements. Terminé lorsque les enregistrements sont traçables jusqu’aux journaux/virements, que dates/heures/devises sont explicites et que les tests empêchent l’exposition de données entre clients.
- [ ] **T18 · Définir le contrat monétaire frontend/backend — R4 ; avant T15–T17.** Revoir DTO, schémas, formulaires et formatage ; utiliser des représentations décimales exactes, une devise explicite, une échelle/un arrondi définis et des calculs backend avec `Money`/`BigDecimal`. Terminé lorsque les tests couvrent les fractions, les limites de précision, les contenus invalides et les allers-retours sans perte ; ne pas utiliser de virgule flottante binaire pour les valeurs monétaires.
- [ ] **T19 · Tester les parcours clients — R3 ; après T10–T18.** Tester connexion → consultation du compte → virement → résultat/historique, ainsi que l’expiration de session, les refus, la déconnexion du backend et la resoumission. Terminé lorsque les parcours passent avec des données synthétiques ; évaluer d’abord tout nouvel outil de test selon `LIBRARY.md`, car le frontend ne possède actuellement aucun script de test.

## P2 — Reprise, rapprochement et contrôles opérationnels

- [ ] **T20 · Contrepassation liée au journal d’origine — R4 ; après T07–T09.** Concevoir les écritures de contrepassation/ajustement avec motifs, permissions et approbations appropriées. Terminé lorsque le journal d’origine reste inchangé, que la contrepassation est équilibrée et liée à sa source, et que les reprises ne peuvent pas la déclencher deux fois.
- [ ] **T21 · Rapprocher le ledger et les projections de solde — R4 ; après T07–T09.** Créer des requêtes/procédures de contrôle des écarts par compte et devise. Terminé lorsque les tests détectent les écarts synthétiques et que des alertes/procédures d’investigation existent ; ne pas corriger automatiquement les soldes sans preuves. Si un traitement planifié est utilisé, documenter son déclencheur, son identité, ses reprises et son comportement en cas de réexécution.
- [ ] **T22 · MFA, récupération d’accès et protection contre les abus — R3 ; après T03, T06.** Implémenter le MFA pour les acteurs/actions exigés par `SECURITY.md`, la réinitialisation du mot de passe/MFA, les limites de fréquence et la surveillance des échecs de connexion. Terminé lorsque les tests couvrent les contournements, les jetons expirés/réutilisés, les identités verrouillées et l’audit ; à terminer avant d’activer les parcours exigeant le MFA.
- [ ] **T23 · Double contrôle maker-checker — R4 ; après T04, T06.** Identifier les actions soumises au double contrôle selon la politique, puis implémenter attente/approbation/rejet/annulation. Terminé lorsque l’initiateur ne peut pas approuver sa propre demande, que le contenu en attente ne peut pas changer silencieusement et qu’un rejet n’entraîne aucune modification financière. C’est un prérequis à l’activation des actions classées comme nécessitant une approbation.
- [ ] **T24 · Définir le périmètre KYC/AML/sanctions — R5.** Consigner les décisions sur les données, les états, les sources de décision, les droits de dérogation et les conditions bloquant l’ouverture de compte/les transactions. Terminé lorsque politique et périmètre sont explicites avant implémentation ; ne pas déduire de réglementation ni intégrer de prestataire sans périmètre défini. La politique convenue alimente T13–T15.
- [ ] **T25 · Normaliser les erreurs et l’observabilité — R3.** Revoir les gestionnaires d’erreurs/clients API, les codes d’erreur stables, les identifiants de corrélation et le masquage. Terminé lorsque les tests empêchent l’exposition de mots de passe/jetons/données personnelles ou de l’existence de ressources hors périmètre, et que des signaux couvrent les échecs de comptabilisation, les refus d’autorisation et les écarts de rapprochement.
- [ ] **T26 · CI et preuves relatives aux dépendances — R3.** Configurer la plateforme CI retenue pour les tests backend, lint/typecheck/build frontend, la détection de secrets, la revue des dépendances/licences et les SBOM de livraison. Revoir la nécessité du dépôt milestone dans `settings.gradle`. Terminé lorsque résultats/artefacts sont conservés, que versions/sources des dépendances sont reproductibles et que les échecs ne sont pas ignorés silencieusement.
- [ ] **T27 · Sauvegarde, restauration et rollback — R4.** Rédiger les procédures et définir RPO/RTO, accès à la base, ordre de livraison et reprise après incident. Terminé lorsqu’un exercice de restauration réussit avec une base synthétique, que ledger/soldes/audit sont vérifiés après reprise et que le rollback applicatif est distingué du traitement des migrations uniquement vers l’avant.

## P3 — Extensions après accord sur les besoins

- [ ] **T28 · Frais, intérêts et fin de journée — R4.** Définir formules, calendriers, base de calcul, échelle/arrondi, dates d’effet et versions produit avant implémentation. Terminé lorsque comptabilisation, réexécutions, contrepassations et rapprochement sont vérifiés sur les cas limites.
- [ ] **T29 · Relevés et exports de données — R3.** Définir le périmètre des rapports et leurs formats ; concevoir permissions, masquage, audit des accès, expiration des téléchargements et limites de volume. Terminé lorsque les tests couvrent les accès entre clients, les injections propres aux formats et les fuites de données.
- [ ] **T30 · Intégrations de paiement externes — R4/R5 selon le périmètre.** Commencer uniquement après sélection des systèmes et contrats de données ; concevoir authentification, signatures des callbacks, protection contre le rejeu, délais, résultats incertains, compensation et règlement/rapprochement. Terminé lorsque les tests en sandbox couvrent les doublons, événements désordonnés et échecs, et que les flux de données sont documentés ; ne jamais connecter les outils locaux à la production.

## Pour commencer

Premier passage : **T01 → T02 → T03 → T04 → T05**, tout en concevant l’audit T06. Terminer ensuite **T07–T09** avant de construire les virements. Satisfaire d’abord les dépendances de chaque tâche ; le classement en P2 ne reporte pas le MFA, les approbations ou les décisions KYC exigés par les parcours P1.

Pour chaque tâche terminée, consigner le responsable, le commit/PR, les commandes de vérification et leurs résultats. À partir de R3, joindre une note d’impact sur la sécurité et des tests négatifs ; à partir de R4, ajouter des preuves sur les transactions, l’idempotence, la concurrence et l’audit, ainsi qu’une note d’impact financier. Ce backlog n’atteste ni l’aptitude à la production ni une conformité certifiée.

## Commandes de vérification prévues lors de l’implémentation

Exécuter les tests backend uniquement après avoir configuré le profil `test` pour utiliser une base PostgreSQL locale réservée aux tests, selon le [guide des environnements](docs/operations/environments.md). Ne pas utiliser de base de production ni de données clients réelles.

Depuis la racine du dépôt :

```powershell
$env:SPRING_PROFILES_ACTIVE = 'test'
.\gradlew.bat test
```

Depuis `customer-web`, après installation des dépendances selon le fichier de verrouillage :

```powershell
pnpm lint
pnpm typecheck
pnpm build
```

Ces commandes sont des étapes de vérification prévues ; elles n’ont pas été exécutées lors de la création ou de la traduction de ce document.
