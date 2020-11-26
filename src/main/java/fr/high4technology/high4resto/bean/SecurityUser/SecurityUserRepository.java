package fr.high4technology.high4resto.bean.SecurityUser;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface SecurityUserRepository extends ReactiveMongoRepository<SecurityUser, String> {

}
