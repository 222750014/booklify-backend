package com.booklify.controller;

import com.booklify.domain.Address;
import com.booklify.service.IAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

class AddressControllerTest {

    private MockMvc mockMvc;
    private IAddressService addressService;

    private final Address sampleAddress = new Address("Buitenkant Street","Woodstock" ,"Cape Town","Western cape","Cape Town"
    );

    @BeforeEach
    void setup() {
        addressService = Mockito.mock(IAddressService.class);
        AddressController controller = new AddressController(addressService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testCreateAddress() throws Exception {
        Mockito.when(addressService.save(any(Address.class))).thenReturn(sampleAddress);

        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "street": "Buitenkant Street",
                        "city": "Cape Town",
                        "suburb": "Woodstock",
                        "province": "Western cape",
                        "country": "South Africa"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postalCode", is("12345")))
                .andExpect(jsonPath("$.city", is("Cape Town")));
    }

    @Test
    void testGetAddressByPostalCode_Found() throws Exception {
        Mockito.when(addressService.findById("12345")).thenReturn(sampleAddress);

        mockMvc.perform(get("/api/addresses/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street", is("Sesona Street")));
    }

    @Test
    void testGetAddressByPostalCode_NotFound() throws Exception {
        Mockito.when(addressService.findById("99999")).thenReturn(null);

        mockMvc.perform(get("/api/addresses/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateAddress() throws Exception {
        Mockito.when(addressService.update(any(Address.class))).thenReturn(sampleAddress);

        mockMvc.perform(put("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                       "street": "Buitenkant Street",
                        "city": "Cape Town",
                        "suburb": "Woodstock",
                        "province": "Western cape",
                        "country": "South Africa"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postalCode", is("12345")));
    }

    @Test
    void testDeleteAddress_Found() throws Exception {
        Mockito.when(addressService.deleteById("12345")).thenReturn(true);

        mockMvc.perform(delete("/api/addresses/12345"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteAddress_NotFound() throws Exception {
        Mockito.when(addressService.deleteById("00000")).thenReturn(false);

        mockMvc.perform(delete("/api/addresses/00000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllAddresses() throws Exception {
        Mockito.when(addressService.getAll()).thenReturn(List.of(sampleAddress));

        mockMvc.perform(get("/api/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postalCode", is("12345")));
    }

    @Test
    void testDeleteAllAddresses() throws Exception {
        mockMvc.perform(delete("/api/addresses"))
                .andExpect(status().isNoContent());
        Mockito.verify(addressService).deleteAll();
    }
}