package com.mlorenzo.photoapp.api.albums.services;

import java.util.List;

import com.mlorenzo.photoapp.api.albums.ui.models.responses.AlbumResponseModel;

public interface AlbumsService {
    List<AlbumResponseModel> getAlbums(String userId);
}
