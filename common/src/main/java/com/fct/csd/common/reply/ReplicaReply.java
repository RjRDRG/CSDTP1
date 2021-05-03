package com.fct.csd.common.reply;

import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.traits.Compactable;
import com.fct.csd.common.traits.Signed;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReplicaReply implements Compactable {

    private Signed<ReplicaReplyBody> body;
    private List<Transaction> missingEntries;

    public ReplicaReply(Signed<ReplicaReplyBody> body, List<Transaction> missingEntries) {
        this.body = body;
        this.missingEntries = missingEntries;
    }

    public ReplicaReply() {
    }

    public Signed<ReplicaReplyBody> getBody() {
        return body;
    }

    public void setBody(Signed<ReplicaReplyBody> body) {
        this.body = body;
    }

    public List<Transaction> getMissingEntries() {
        return missingEntries;
    }

    public void setMissingEntries(List<Transaction> missingEntries) {
        this.missingEntries = missingEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplicaReply that = (ReplicaReply) o;
        return body.equals(that.body) && Arrays.equals(missingEntries, that.missingEntries);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(body);
        result = 31 * result + Arrays.hashCode(missingEntries);
        return result;
    }

    @Override
    public String toString() {
        return "ReplicaReply{" +
                "body=" + body +
                ", missingEntries=" + Arrays.toString(missingEntries) +
                '}';
    }
}
