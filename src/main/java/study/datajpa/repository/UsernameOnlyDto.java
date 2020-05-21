package study.datajpa.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UsernameOnlyDto {

    private final String username;
}
