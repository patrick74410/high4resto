package fr.high4technology.high4resto.bean.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.bean.SecurityUser.SecurityUserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    @Autowired
    private ClientRepository clients;
    @Autowired
    private SecurityUserRepository security;

    @GetMapping("/get/{idClient}/{securityKey}")
	public Mono<Client> getById(@PathVariable String idClient,@PathVariable String securityKey){
        return this.security.findById(idClient).flatMap(security_user->{
            if(security_user.getGenerateKey().equals(securityKey))
            {
                return clients.findById(idClient);
            }
            else
            {
                return Mono.just(Client.builder().build());
            }
        });
    }

    @PutMapping("/update/{securityKey}")
    Mono<Client> update(@RequestBody Client client,@PathVariable String securityKey)
    {
        return this.security.findById(client.getId()).flatMap(security_user->{
            if(security_user.getGenerateKey().equals(securityKey))
            {
                return clients.findById(client.getId());
            }
            else
            {
                return Mono.just(Client.builder().id("anonymous").build());               
            }
        }).map(foundItem->{
            if(!foundItem.getId().equals("anonymous"))
            {
                foundItem.setAdresseL1(client.getAdresseL1());
                foundItem.setAdresseL2(client.getAdresseL2());
                foundItem.setCity(client.getCity());
                foundItem.setCurrentPanier(client.getCurrentPanier());
                foundItem.setEmail(client.getEmail());
                foundItem.setName(client.getName());
                foundItem.setSendInfo(client.isSendInfo());
            }   
            return foundItem;
        }).flatMap(clients::save);
    }
}
