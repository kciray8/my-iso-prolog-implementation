package iaroslav.baranov.tracklog.web.api.toplevel;

import java.util.List;

public record QueryResponse(
        List<Answer> answers
) {
}
