package com.mlorenzo.photoapp.api.users.services;

import java.util.List;

import com.mlorenzo.photoapp.api.users.ui.models.responses.AlbumResponseModel;

public interface AlbumService {
	List<AlbumResponseModel> getUserAlbums(String userId);
}
