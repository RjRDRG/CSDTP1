package com.fct.csd.common.reply;

import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.traits.Compactable;
import com.fct.csd.common.traits.Result;
import com.fct.csd.common.traits.Signed;

import java.io.Serializable;
import java.util.Arrays;

public class ReplicatedReply implements Compactable {

    private Signed<ReplicaReplyBody>[] replies;
    private Transaction[] missingEntries;

    public ReplicatedReply(Signed<ReplicaReplyBody>[] replies, Transaction[] missingEntries) {
        this.replies = replies;
        this.missingEntries = missingEntries;
    }

    public <T extends Serializable> Result<T> extractReply() {
        Result<byte[]> result = replies[0].getData().getReply();
        if(result.isOK())
            return Result.ok(Compactable.decompact(result.value()));
        else
            return Result.error(result.error());
    }

    public Signed<ReplicaReplyBody>[] getReplies() {
        return replies;
    }

    private void setReplies(Signed<ReplicaReplyBody>[] replies) {
        this.replies = replies;
    }

    public Transaction[] getMissingEntries() {
        return missingEntries;
    }

    private void setMissingEntries(Transaction[] missingEntries) {
        this.missingEntries = missingEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplicatedReply that = (ReplicatedReply) o;
        return Arrays.equals(replies, that.replies) && Arrays.equals(missingEntries, that.missingEntries);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(replies);
        result = 31 * result + Arrays.hashCode(missingEntries);
        return result;
    }

    @Override
    public String toString() {
        return "ReplicatedReply{" +
                "replies=" + Arrays.toString(replies) +
                ", missingEntries=" + Arrays.toString(missingEntries) +
                '}';
    }
}
