package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class RemindersProcessedEvent extends Event {

    @Override
    public String getCode() {

        return "REMINDERS-PROCESSED";
    }

    public RemindersProcessedEvent setDurationToProcessAllMessages(long durationToProcessAllMessages) {

        params.put("duration-to-process-reminders-ms", String.valueOf(durationToProcessAllMessages));
        return this;
    }

    public RemindersProcessedEvent setAmountOfMessagesSuccessfullyProcessed(int amountOfMessagesProcessed) {

        params.put("amount-of-reminders-successfully-processed", String.valueOf(amountOfMessagesProcessed));
        return this;
    }

    public RemindersProcessedEvent setAmountOfMessagesFailedToProcess(int amountOfMessagesFailedToProcess) {

        params.put("amount-of-reminders-failed-to-process", String.valueOf(amountOfMessagesFailedToProcess));
        return this;
    }
}
