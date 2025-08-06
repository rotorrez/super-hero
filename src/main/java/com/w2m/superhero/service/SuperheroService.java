@Test
void shouldThrowWebApplicationExceptionWhenCallFails() {
    // Given: a valid token request
    RequestTokenDTO request = RequestTokenDTO.builder()
        .realm("realm")
        .password("fail")
        .build();

    // And a mocked response with status and body
    Response mockedResponse = Response
        .status(Response.Status.BAD_REQUEST)
        .entity("Invalid credentials")
        .build();

    WebApplicationException exception = new WebApplicationException(mockedResponse);

    TokenRestClient mockClient = mock(TokenRestClient.class);
    when(mockClient.getToken(any())).thenThrow(exception);

    // When & Then
    WebApplicationException thrown = assertThrows(WebApplicationException.class, () -> {
        mockClient.getToken(request);
    });

    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), thrown.getResponse().getStatus());
    assertEquals("Invalid credentials", thrown.getResponse().readEntity(String.class));
}







@Test
void shouldThrowGenericExceptionWhenUnexpectedErrorOccurs() {
    // Given: a valid token request DTO
    RequestTokenDTO request = RequestTokenDTO.builder()
        .realm("realm")
        .password("pass")
        .build();

    // And the mock client throws a RuntimeException
    when(tokenRestClient.getToken(any(RequestTokenDTO.class)))
        .thenThrow(new RuntimeException("Something went wrong"));

    // When & Then: the TokenProvider should propagate the exception
    RuntimeException thrown = assertThrows(
        RuntimeException.class,
        () -> tokenProvider.getBearerToken()
    );

    assertEquals("Something went wrong", thrown.getMessage());
    verify(tokenRestClient).getToken(any(RequestTokenDTO.class));
}
