package com.fct.csd.common.request;

import com.fct.csd.common.cryptography.key.EncodedPublicKey;
import com.fct.csd.common.traits.Compactable;

public interface OrderedRequest extends Compactable{
    byte[] getClientId();
    EncodedPublicKey getClientPublicKey();
}
