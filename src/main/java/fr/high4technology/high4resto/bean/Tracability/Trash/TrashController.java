package fr.high4technology.high4resto.bean.Tracability.Trash;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.Util.Util;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/trash")
@RequiredArgsConstructor
public class TrashController {
	@Autowired
	private TrashRepository trashs;

	@GetMapping("/find/")
	public Flux<Trash> getAllAll() {
		return trashs.findAll();
	}

	@PutMapping("/insert/")
	Mono<Trash> insert(@RequestBody Trash trash) {
		System.out.println("xcvxcvxcv" + trash.getDelevery().getToDelivery().getPrepare());
		trash.setInside(Util.getTimeNow());
		return trashs.save(trash);
	}
}
