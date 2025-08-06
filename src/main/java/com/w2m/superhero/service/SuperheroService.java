@Test
void shouldThrowWebApplicationExceptionWhenCallFails() {
    // Arrange
    String errorBody = "Unauthorized";
    int httpStatus = 401;

    Response mockResponse = mock(Response.class);
    when(mockResponse.getStatus()).thenReturn(httpStatus);
    when(mockResponse.readEntity(String.class)).thenReturn(errorBody);

    WebApplicationException exception = new WebApplicationException(mockResponse);

    when(tokenRestClient.getToken(any(RequestTokenDTO.class)))
        .thenThrow(exception);

    // Act & Assert
    WebApplicationException thrown = assertThrows(
        WebApplicationException.class,
        () -> tokenProvider.getBearerToken()
    );

    assertEquals(httpStatus, thrown.getResponse().getStatus());
    verify(tokenRestClient).getToken(any(RequestTokenDTO.class));
}



@Test
void shouldThrowGenericExceptionWhenUnexpectedErrorOccurs() {
    // Arrange
    when(tokenRestClient.getToken(any(RequestTokenDTO.class)))
        .thenThrow(new RuntimeException("Something went wrong"));

    // Act & Assert
    RuntimeException thrown = assertThrows(
        RuntimeException.class,
        () -> tokenProvider.getBearerToken()
    );

    assertEquals("Something went wrong", thrown.getMessage());
    verify(tokenRestClient).getToken(any(RequestTokenDTO.class));
}
