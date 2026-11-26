# Lumiere

App Android para registrar e avaliar os filmes que você já assistiu.

## Funcionalidades
- Filmes populares e busca por título
- Detalhes do filme com sinopse e elenco
- Marcar como "Já vi", "Quero ver" e "Favorito", com nota em estrelas
- Perfil com estatísticas

## Tecnologias
Kotlin · XML · ViewBinding · MVVM · Navigation Component · Retrofit · Room · Coroutines/Flow · Glide · Firebase Authentication

## Como rodar
1. Crie uma chave de API no TMDB e adicione `TMDB_API_KEY=sua_chave` ao arquivo `local.properties`
2. Crie um projeto no [Firebase](https://console.firebase.google.com), registre um app Android com o mesmo `applicationId` do projeto e ative o login por e-mail e senha (Authentication)
3. Baixe o `google-services.json` e coloque na pasta `app/`
4. Rode o app pelo Android Studio

## Créditos
This product uses the TMDB API but is not endorsed or certified by TMDB.