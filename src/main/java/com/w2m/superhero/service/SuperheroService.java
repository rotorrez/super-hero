@Test
void getConfiguration_shouldThrow_whenClientFails() {
    when(tokenProvider.getBearerToken()).thenReturn("token");

    // Mock del Response
    Response mockResponse = mock(Response.class);
    when(mockResponse.getStatus()).thenReturn(500);
    when(mockResponse.readEntity(String.class)).thenReturn("Internal error");

    // Mock del WebApplicationException
    WebApplicationException exception = mock(WebApplicationException.class);
    when(exception.getResponse()).thenReturn(mockResponse);

    when(restClient.callGetConfiguration(any(), any(), any(), any()))
        .thenThrow(exception);

    // Act + Assert
    WebApplicationException thrown = assertThrows(WebApplicationException.class, () ->
        service.getConfiguration("PROC123", "CONF123")
    );

    assertEquals(500, thrown.getResponse().getStatus());
}
