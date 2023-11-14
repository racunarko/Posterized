package hr.fer.progi.posterized.rest;

import hr.fer.progi.posterized.dao.OsobaRepository;
import hr.fer.progi.posterized.domain.Osoba;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

@Service
public class KorisnikUserDetailsService implements UserDetailsService {
    @Value("${hr.fer.progi.posterized.admin.password}")
    private String adminLozinkaHash;

    @Autowired
    private OsobaRepository osobaRepository;
    @Override
    public UserDetails loadUserByUsername(String username) {
        Osoba korisnik = osobaRepository.findByEmail(username);
        if ("admin".equals(username)) {
            return new User(
                    username,
                    adminLozinkaHash,
                    commaSeparatedStringToAuthorityList("ROLE_ADMIN")
            );}
        if (korisnik != null) {
            return new User(
                    korisnik.getEmail(),
                    korisnik.getLozinka(),
                    commaSeparatedStringToAuthorityList("ROLE_USER")
            );
        } else {
            throw new UsernameNotFoundException("No user " + username);
        }
    }
}
