package com.fct.csd.common.request;

import com.fct.csd.common.item.Block;

import java.io.Serializable;

public class MineRequestBody implements Serializable {
    private Block block;

    public MineRequestBody(Block block) {
        this.block = block;
    }

    public MineRequestBody() {
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    @Override
    public String toString() {
        return "MineRequestBody{" +
                "block=" + block +
                '}';
    }
}
