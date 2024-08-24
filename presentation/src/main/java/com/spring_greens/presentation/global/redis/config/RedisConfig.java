package com.spring_greens.presentation.global.redis.config;
import com.spring_greens.presentation.global.redis.stream.RedisPublisher;
import com.spring_greens.presentation.global.redis.stream.RedisSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.ArrayList;
import java.util.List;


@Configuration
public class RedisConfig {
    /**
     * Host, Port, Password will require to connect redis database. <br>
     * generally, Redis port is 6379 if you need to change port to other port, you will need to change redis configuration file. <br>
     * password is very important to prevent what other access to redis server. so before you redis server change to redis configuration file.<br>
     * @author itstime0809
     */
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.password}")
    private String password;

    /**
     * This method will set redisConfiguration between synchronous and asynchronous. <br>
     * but this setting won't be changed manually. because spring application set multi-thread environment. <br>
     * now currently using 'Lettuce'.
     *
     * @author itstime0809
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        redisConfiguration.setPassword(password);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    /**
     * RedisTemplate can be used redis operations. <br>
     * but there is something to be aware of that 'redisJsonTemplate' can be used saving products that extract by schedule algorithms. <br>
     * however, 'redisHashTemplate' can be used increase product view count. <br>
     * therefore, Those should be used according to the situation.
     * <br>
     *
     * StringRedisSerializer is an implementation of RedisSerializer T. <br>
     * Redis data-structure based on key, value. <br>
     * it is serialized by RedisSerializer when you want to save any data type into redis cache. <br>
     * RedisSerializer accept T generic type. so StringRedisSerializer serialize data to String type. <br>
     * finally, serialized data translate to byte[] and save redis cache. <br>
     *
     * If you want to make custom Template, extends RedisTemplate.<br>
     * but I think that we enough to use StringRedisSerializer and ObjectRedisSerializer( Jackson etc ). <br>
     *
     * @author itstime0809
     */

    @Bean
    public RedisTemplate<String, Object> redisJsonTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, Long> redisHashTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory, RedisSubscriber redisSubscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisSubscriber, registerTopic());
        return container;
    }

    @Bean
    public RedisPublisher redisPublisher(StringRedisTemplate redisTemplate) {
        return new RedisPublisher(redisTemplate);
    }

    @Bean
    public List<Topic> registerTopic() {
        List<Topic> topics = new ArrayList<>();
        topics.add(new ChannelTopic("/apm"));
        topics.add(new ChannelTopic("/chung"));
        topics.add(new ChannelTopic("/dong"));
        topics.add(new ChannelTopic("/jeil"));
        return topics;
    }
}
