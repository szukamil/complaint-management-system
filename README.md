# Complaint Management System

System zarządzania reklamacjami klientów - zadanie rekrutacyjne.

## Opis projektu

Aplikacja umożliwia zarządzanie reklamacjami klientów poprzez RESTowe API, które pozwala na:
- Dodawanie nowych reklamacji
- Edycję treści reklamacji
- Pobieranie zapisanych wcześniej reklamacji

Każda reklamacja zawiera:
- Identyfikator reklamowanego produktu
- Treść reklamacji
- Datę utworzenia
- Dane zgłaszającego reklamację
- Kraj (wykrywany automatycznie na podstawie IP)
- Licznik zgłoszeń

System zapewnia unikalność reklamacji po identyfikatorze produktu i zgłaszającym. W przypadku próby dodania duplikatu, zwiększane jest pole licznik, bez edycji pozostałych danych.

## Technologie

- Java 17
- Spring Boot 3.x
- Maven
- PostgreSQL
- Liquibase
- Lombok
- Docker & Docker Compose
- TestContainers

## Struktura projektu

Projekt jest zorganizowany zgodnie z dobrymi praktykami programistycznymi, z jasnym podziałem na warstwy:
- Controller - obsługa żądań HTTP
- Service - logika biznesowa
- Repository - dostęp do bazy danych
- Model - encje bazodanowe
- DTO - obiekty transferu danych
- Exception - obsługa wyjątków

## Uruchomienie aplikacji

### Wymagania

- Java 17
- Maven
- Docker i Docker Compose (opcjonalnie)

### Uruchomienie lokalne

1. Uruchom bazę danych PostgreSQL:
   ```bash
   docker-compose up postgres -d
   ```

2. Uruchom aplikację z profilem lokalnym:
   ```bash
   ./mvnw spring-boot:run -Dspring.profiles.active=local
   ```

### Uruchomienie przy użyciu Docker Compose

1. Zbuduj i uruchom wszystkie usługi:
   ```bash
   docker-compose up --build
   ```

### Uruchomienie testów

```bash
./mvnw clean test
```

## Endpointy API

### Tworzenie reklamacji
```
POST /api/complaints
Content-Type: application/json

{
  "productId": "product-123",
  "content": "Reklamacja na produkt",
  "reporter": "jan.kowalski@email.com"
}
```

### Aktualizacja treści reklamacji
```
PUT /api/complaints/{id}
Content-Type: application/json

{
  "content": "Nowa treść reklamacji"
}
```

### Pobieranie reklamacji po ID
```
GET /api/complaints/{id}
```

### Pobieranie wszystkich reklamacji
```
GET /api/complaints
```

### Pobieranie reklamacji z paginacją
```
GET /api/complaints/paginated?page=0&size=10&sort=createdAt&direction=DESC
```

## Konfiguracja

Aplikacja posiada trzy profile konfiguracyjne:
- `local` - do lokalnego rozwoju
- `test` - używany w testach automatycznych
- `prod` - konfiguracja produkcyjna

## Zaawansowane funkcje

Oprócz podstawowej funkcjonalności zarządzania reklamacjami, projekt zawiera szereg zaawansowanych funkcji:

### 1. Dokumentacja API (OpenAPI/Swagger)

Aplikacja zawiera automatycznie generowaną dokumentację API opartą na OpenAPI 3.0. Dostęp do dokumentacji:

```
http://localhost:8080/swagger-ui/index.html
```

### 2. Paginacja i Sortowanie

Aplikacja wspiera zaawansowane funkcje paginacji i sortowania dla endpointu listowania reklamacji:

```
GET /api/complaints/paginated?page=0&size=10&sort=createdAt&direction=DESC
```

### 3. Monitoring i Metryki (Micrometer/Prometheus)

Aplikacja zawiera integrację z Micrometer do zbierania metryk, które można eksportować do Prometheus:

- Czas wykonania kluczowych operacji biznesowych
- Liczba zapytań do API
- Statystyki połączeń z bazą danych

Metryki są dostępne pod:

```
http://localhost:8080/actuator/prometheus
```

### 4. Pamięć Podręczna (Cache)

Zaimplementowano cachowanie dla często wykorzystywanych operacji:
- Pobieranie reklamacji po ID
- Określanie kraju na podstawie adresu IP

### 5. Bezpieczeństwo (Spring Security)

Aplikacja zawiera podstawowe zabezpieczenia:
- Autoryzacja na poziomie endpointów
- Role ADMIN i USER z różnymi uprawnieniami
- Podstawowe uwierzytelnianie HTTP

Dostępni użytkownicy:
- user / user123 (rola USER)
- admin / admin123 (rola ADMIN)

### 6. Internacjonalizacja (i18n)

Aplikacja wspiera wielojęzyczność:
- Komunikaty błędów w różnych językach
- Wybór języka na podstawie nagłówka Accept-Language

### 7. Health Check

Zaawansowane sprawdzanie stanu aplikacji, dostępne pod:

```
http://localhost:8080/actuator/health
```

## Dodatkowe instrukcje konfiguracji

### GeoIP Database

Aplikacja używa bazy danych MaxMind GeoLite2 do określania kraju na podstawie adresu IP. Aby uruchomić aplikację, musisz dodać tę bazę do projektu lub użyć uproszczonej implementacji.

#### Sposób 1: Pobranie i dodanie pliku GeoLite2-Country.mmdb

1. Odwiedź stronę MaxMind i utwórz bezpłatne konto: https://www.maxmind.com/en/geolite2/signup
2. Po zalogowaniu, pobierz bazę danych w formacie Binary/mmdb:
   - "GeoLite2 Country"
3. Umieść pobrany i rozpakowany plik `GeoLite2-Country.mmdb` w katalogu:
   ```
   src/main/resources/
   ```

#### Sposób 2: Użycie uproszczonej implementacji LocationService

Jeśli nie chcesz pobierać bazy GeoIP, możesz użyć uproszczonej implementacji `LocationServiceImpl`, która zawiera wstępnie zdefiniowane mapowania IP do krajów i generuje pseudolosowe kody krajów dla nieznanych adresów IP.

## Dobre praktyki

Projekt został zaimplementowany zgodnie z dobrymi praktykami:
- Clean code i SOLID
- Walidacja danych wejściowych
- Obsługa wyjątków
- Bezpieczne API
- Testy jednostkowe i integracyjne
- Dokumentacja
- Dockeryzacja

## Rozwiązywanie problemów

### Problem: java.io.FileNotFoundException: class path resource [GeoLite2-Country.mmdb]

Ten błąd występuje, gdy aplikacja nie może znaleźć pliku bazy danych GeoIP. Aby rozwiązać ten problem:

1. Sprawdź, czy plik `GeoLite2-Country.mmdb` znajduje się w katalogu `src/main/resources/`
2. Jeśli nie chcesz używać rzeczywistej bazy GeoIP, skorzystaj z uproszczonej implementacji LocationService