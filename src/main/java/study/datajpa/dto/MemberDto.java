package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import study.datajpa.entity.Member;

@ToString
@Getter
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Member member) {

        id = member.getId();
        username = member.getUsername();

        if (member.getTeam() != null) {
            teamName = member.getTeam().getName();
        }
    }
}
