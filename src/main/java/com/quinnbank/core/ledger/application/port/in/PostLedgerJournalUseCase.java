package com.quinnbank.core.ledger.application.port.in;

import com.quinnbank.core.ledger.application.command.PostLedgerJournalCommand;
import com.quinnbank.core.ledger.application.result.LedgerJournalSnapshot;

public interface PostLedgerJournalUseCase {

    LedgerJournalSnapshot post(PostLedgerJournalCommand command);
}
