package se.ifmo.origin_backend.event;

import java.util.List;

public record OrgBulkEvent(String type, List<Integer> ids) {}
