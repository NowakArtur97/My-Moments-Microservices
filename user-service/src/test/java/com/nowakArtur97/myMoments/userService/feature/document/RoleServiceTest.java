package com.nowakArtur97.myMoments.userService.feature.document;

import com.nowakArtur97.myMoments.userService.feature.testBuilder.RoleTestBuilder;
import com.nowakArtur97.myMoments.userService.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("RoleService_Tests")
class RoleServiceTest {

    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {

        roleService = new RoleService(roleRepository);
    }

    @Test
    void when_find_role_by_name_should_return_role() {

        String roleName = "USER_ROLE";

        RoleDocument roleDocumentExpected = RoleTestBuilder.DEFAULT_ROLE_ENTITY;

        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleDocumentExpected));

        RoleDocument roleDocumentActual = roleService.findByName(roleName).get();

        assertAll(() -> assertEquals(roleDocumentExpected, roleDocumentActual,
                () -> "should return role: " + roleDocumentExpected + ", but was: empty"),
                () -> assertEquals(roleDocumentExpected.getName(), roleDocumentActual.getName(),
                        () -> "should return role with name: " + roleDocumentExpected.getName()
                                + ", but was: " + roleDocumentActual.getName()),
                () -> verify(roleRepository, times(1)).findByName(roleName),
                () -> verifyNoMoreInteractions(roleRepository));
    }

    @Test
    void when_find_not_existing_role_by_name_should_return_empty_optional() {

        String roleName = "ROLEee_USER";

        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        Optional<RoleDocument> roleDocumentActualOptional = roleService.findByName(roleName);

        assertAll(
                () -> assertTrue(roleDocumentActualOptional.isEmpty(),
                        () -> "should return empty optional, but was: " + roleDocumentActualOptional.get()),
                () -> verify(roleRepository, times(1)).findByName(roleName),
                () -> verifyNoMoreInteractions(roleRepository));
    }
}
