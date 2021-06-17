package com.fct.csd.common.request;

import java.io.Serializable;

public enum LedgerOperation implements Serializable {
    PULL, MINE, INSTALL, OBTAIN, TRANSFER;
}
