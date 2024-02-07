package webflux;

import com.study.gateway.database.RedisConfig;
import com.study.gateway.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {JwtService.class, RedisConfig.class})
public class WebFluxTest {
    @Autowired
    JwtService jwtService;

    @Test
    public void monoAndFluxTest() {
        Mono<String> mono = Mono.just("asd");
        mono.filter(StringUtils::isBlank)
                .filter(StringUtils::isNotBlank)
//                .switchIfEmpty(Mono.error(new RuntimeException("없음")))
                .subscribe();


    }

    @Test
    public void joiningTest() {
        List<Type> types = List.of(new Type("asd"), new Type("asdasd"));

        String collect = types.stream()
                .map(t -> t.str)
                .collect(Collectors.joining(","));
        System.out.println(collect);
    }

    class Type{
        String str;

        public Type(String str) {
            this.str = str;
        }
    }



}
