-Financeapp projekat

Ovaj projekat je urađen za predmet Programiranje u Javi (PUJ).  
Zadatak je bio izgraditi desktop aplikaciju za praćenje ličnih finansija koristeći:

- Java Swing (IntelliJ GUI Designer)
- MongoDB baza podataka
- MVC princip (osnovna podjela logike po klasama)
- Implementirane CRUD operacije: dodavanje, čitanje, ažuriranje i brisanje transakcija

Aplikacija omogućava unos prihoda i rashoda, kategorizaciju transakcija, prikaz podataka u tabeli te export izvještaja u TXT formatu.


-Funkcionalnosti

Dodavanje transakcije
- Unos iznosa, opisa, vrste (Prihod/Rashod) i kategorije
- Snimanje podataka u MongoDB
- Automatsko prikazivanje u tabeli
- Ažuriranje sažetog prikaza (Prihod, Rashod, Saldo)



-Ažuriranje transakcije
- Korisnik selektuje red iz tabele
- Podaci se učitavaju u formu
- Nakon izmjene, klikom na Ažuriraj transakcija se mijenja u bazi
- Tabela se automatski osvježava



-Brisanje transakcije
- Selektovan red se briše iz MongoDB-a
- Prije brisanja prikazuje se dijaloški prozor za potvrdu
- Nakon potvrde vrši se refresh podataka



-Kategorije transakcija
Podržane kategorije su:
- Plata  
- Hrana  
- Racuni  
- Zabava  
- Prijevoz  
- Ostalo  

Svaka transakcija može biti svrstana u jednu od ovih kategorija.


-Export finansijskih podataka (TXT)
Aplikacija kreira izvještaj koji sadrži:

- Ukupni prihod  
- Ukupni rashod  
- Stanje (saldo)  
- Rashode po kategorijama  

Format exporta je lako čitljiv, npr:

Ukupni prihod: 1500
Ukupni rashod: 900
Stanje: 600
Rashodi po kategorijama:
Hrana: 200
Prevoz: 150
Zabava: 100
Racuni: 450


