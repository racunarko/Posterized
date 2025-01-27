package hr.fer.progi.posterized.service.impl;

import hr.fer.progi.posterized.dao.FotografijaRepository;
import hr.fer.progi.posterized.domain.Fotografija;
import hr.fer.progi.posterized.domain.Konferencija;
import hr.fer.progi.posterized.domain.Media;
import hr.fer.progi.posterized.service.FotografijaService;
import hr.fer.progi.posterized.service.KonferencijaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FotografijaServiceJPA implements FotografijaService {
    @Autowired
    KonferencijaService konfService;
    @Autowired
    FotografijaRepository fotoRepo;

    @Override
    public void spremiSlike(String nazivKonf, String admin, List<MultipartFile> slike){
        Konferencija konf = konfService.findByNazivIgnoreCase(nazivKonf);
        if(konf == null) Assert.hasText("","Konferencija s nazivom " + nazivKonf + " ne postoji.");
        if(!konf.getAdminKonf().getEmail().equalsIgnoreCase(admin)) Assert.hasText("","Nemate pristup ovoj konferenciji.");
        Media objekt = new Media();
        for(MultipartFile slika: slike){
            String naziv = UUID.randomUUID().toString();
            String url = objekt.upload(slika, naziv, nazivKonf+"/fotografije");
            Fotografija foto = new Fotografija();
            foto.setKonferencija(konf);
            foto.setUrlSlike(url);
            fotoRepo.save(foto);
        }
    }

    @Override
    public List<String> dohvatiSlike(Integer pin) {
        Konferencija konf = konfService.findByPin(pin);
        if(konf == null) Assert.hasText("","Konferencija s pinom " + pin + " ne postoji.");
        List<Fotografija> slike = fotoRepo.findAllByKonferencija(konf);
        List<String> rez = new ArrayList<>();
        for(Fotografija slika : slike){
            rez.add(slika.getUrlSlike());
        }
        return rez;
    }

    @Override
    public byte[] preuzmi(String url) throws IOException {
        URL imageUrl = new URL(url);
        try (InputStream inputStream = imageUrl.openStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }
}
