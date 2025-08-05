@ExtendWith(MockitoExtension.class)
class ProcessConfigurationServiceTest {

    @InjectMocks
    ProcessConfigurationService service;

    @Mock
    TokenProvider tokenProvider;

    @Mock
    ProcessConfigurationRestClient restClient;

    @BeforeEach
    void setUp() {
        // Si tienes ConfigProperty, lo puedes setear con reflexión si no usas CDI
        ReflectionTestUtils.setField(service, "clientId", "test-client-id");
    }

    @Test
    void getConfiguration_shouldReturnDTO_whenCallSucceeds() {
        String processId = "PROC123";
        String configId = "CONF456";
        String token = "fake-token";
        String bearer = "Bearer " + token;

        ProcessConfigurationDTO expected = new ProcessConfigurationDTO();
        expected.setConfigurationId(configId);

        when(tokenProvider.getBearerToken()).thenReturn(token);
        when(restClient.callGetConfiguration(processId, configId, bearer, "test-client-id")).thenReturn(expected);

        ProcessConfigurationDTO result = service.getConfiguration(processId, configId);

        assertNotNull(result);
        assertEquals(configId, result.getConfigurationId());
        verify(tokenProvider).getBearerToken();
        verify(restClient).callGetConfiguration(processId, configId, bearer, "test-client-id");
    }

    @Test
    void getConfiguration_shouldThrow_whenClientFails() {
        when(tokenProvider.getBearerToken()).thenReturn("token");
        when(restClient.callGetConfiguration(any(), any(), any(), any()))
            .thenThrow(new WebApplicationException(Response.status(500).entity("Internal error").build()));

        assertThrows(WebApplicationException.class, () ->
            service.getConfiguration("PROC123", "CONF123")
        );
    }
}
