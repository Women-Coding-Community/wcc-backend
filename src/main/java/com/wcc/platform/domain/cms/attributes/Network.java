package com.wcc.platform.domain.cms.attributes;

/**
 * Network Pojo class to be returned to frontend.
 *
 * @param type class name of the network based in the frontend pre-defined types.
 * @param link uri link to the specific network.
 */
public record Network(String type, String link) {}
// TODO change network type to an enum of avaialables classNames.
