package com.javarush.jira.profile.internal.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.model.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.javarush.jira.common.util.JsonUtil.writeValue;
import static com.javarush.jira.login.internal.web.UserTestData.MANAGER_MAIL;
import static com.javarush.jira.login.internal.web.UserTestData.USER_MAIL;
import static com.javarush.jira.profile.internal.web.ProfileTestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ProfileRestControllerTest extends AbstractControllerTest {

    public static final String REST_URL = "/api/profile";

    @Test
    @WithUserDetails(value = USER_MAIL)
    void shouldGetValidResult() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void shouldGetNotValidResult() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/"))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    void shouldGetUnAuthorizedResult() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectIfUserNoAuthentication() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = MANAGER_MAIL)
    void shouldStatusNoContentIfProfileIsEmpty() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(GUEST_PROFILE_EMPTY_TO)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = MANAGER_MAIL)
    void shouldUpdateValidResult() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(USER_PROFILE_TO)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = MANAGER_MAIL)
    void shouldUpdateNotValidResult() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(getInvalidTo())))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails(value = MANAGER_MAIL)
    void shouldSuccessUpdateProfileResult() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(getUpdated(1))))
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = MANAGER_MAIL)
    void shouldGetNewProfileToResult() throws Exception {
        ProfileTo profileTo = getNewTo();
        profileTo.setId(2L);
        perform(MockMvcRequestBuilders.get(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(profileTo)))
                .andDo(print())
                .andExpect(status().isOk());
        Profile expected = getNew(profileTo.getId());
        PROFILE_MATCHER.assertMatch(expected, getNew(expected.getId()));
    }

    @Test
    @WithUserDetails(value = MANAGER_MAIL)
    void shouldUpdateProfileToResult() throws Exception {
        ProfileTo profileTo = getUpdatedTo();
        profileTo.setId(10L);
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(profileTo)))
                .andDo(print());
        Profile expected = getUpdated(profileTo.getId());
        PROFILE_MATCHER.assertMatch(expected, getUpdated(expected.getId()));
    }
}