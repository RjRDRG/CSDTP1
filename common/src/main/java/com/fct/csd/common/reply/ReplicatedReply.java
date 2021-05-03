package com.fct.csd.common.reply;

import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.traits.Compactable;
import com.fct.csd.common.traits.Result;
import com.fct.csd.common.traits.Signed;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReplicatedReply implements Compactable {

    private List<Signed<ReplicaReplyBody>> replies;
    private List<Transaction> missingEntries;

    public ReplicatedReply(List<Signed<ReplicaReplyBody>> replies, List<Transaction> missingEntries) {
        this.replies = replies;
        this.missingEntries = missingEntries;
    }

    public <T> Result<T> extractReply() {
        Result<byte[]> result = replies.get(0).getData().getReply();
        if(result.isOK())
            return Result.ok(Compactable.decompact(result.value()));
        else
            return Result.error(result.error());
    }

    public List<Signed<ReplicaReplyBody>> getReplies() {
        return replies;
    }

    private void setReplies(List<Signed<ReplicaReplyBody>> replies) {
        this.replies = replies;
    }

    public List<Transaction> getMissingEntries() {
        return missingEntries;
    }

    private void setMissingEntries(List<Transaction> missingEntries) {
        this.missingEntries = missingEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplicatedReply reply = (ReplicatedReply) o;
        return replies.equals(reply.replies) && missingEntries.equals(reply.missingEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(replies, missingEntries);
    }

    @Override
    public String toString() {
        return "ReplicatedReply{" +
                "replies=" + replies +
                ", missingEntries=" + missingEntries +
                '}';
    }
}
