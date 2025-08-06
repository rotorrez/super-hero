@Test
void shouldForceNewTokenWhenExpired() {
    // Given: a cached token that's already expired
    ResponseTokenDTO expiredToken = new ResponseTokenDTO();
    expiredToken.setJwt(createJwtWithExpiration(Instant.now().minusSeconds(60))); // ya expirado
    cachedTokenRef.set(expiredToken);

    // Given: the new token to return
    ResponseTokenDTO newToken = new ResponseTokenDTO();
    newToken.setJwt(createJwtWithExpiration(Instant.now().plusSeconds(3600))); // válido por 1 hora

    when(tokenRestClient.getToken(any(RequestTokenDTO.class))).thenReturn(newToken);

    // When
    String bearerToken = tokenProvider.getBearerToken();

    // Then
    assertEquals("Bearer " + newToken.getJwt(), bearerToken);
    verify(tokenRestClient).getToken(any(RequestTokenDTO.class));
}
