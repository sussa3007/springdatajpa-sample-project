package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testMemer() {
        // Given
        Member member = new Member();
        member.setUsername("mamberA");
        // When
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        // Then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        /* 같은 객체로 인식한다. 하나의 트랜잭션 안에서 조회하는 것이기 때문에- 영속성*/
        Assertions.assertThat(findMember).isEqualTo(member);
    }

}