package com.debbech.spongebob.model;

public enum TrackStatus {
    DOWNLOADED,
    IN_REVIEW, //got processed successfully and didn't yet decide on whether copyrighted or not
    UNPROCESSED, //not processed yet, will be
    FAILED, //failed to be processed for some reason
    COPYRIGHTED,
    NOT_COPYRIGHTED,
    UNKNOWN, // when a track is done processing and couldn't decide whether we should classify it as copyrighted or not
    LOST // when it fails for some reason
}
