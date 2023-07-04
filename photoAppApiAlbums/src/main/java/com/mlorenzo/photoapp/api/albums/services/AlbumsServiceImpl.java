package com.mlorenzo.photoapp.api.albums.services;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.mlorenzo.photoapp.api.albums.data.AlbumEntity;
import com.mlorenzo.photoapp.api.albums.ui.models.responses.AlbumResponseModel;

@Service
public class AlbumsServiceImpl implements AlbumsService {

    @Override
    public List<AlbumResponseModel> getAlbums(String userId) {
        List<AlbumEntity> albumsEntities = new ArrayList<>();
        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setUserId(userId);
        albumEntity.setAlbumId("album1Id");
        albumEntity.setDescription("album 1 description");
        albumEntity.setId(1L);
        albumEntity.setName("album 1 name");
        AlbumEntity albumEntity2 = new AlbumEntity();
        albumEntity2.setUserId(userId);
        albumEntity2.setAlbumId("album2Id");
        albumEntity2.setDescription("album 2 description");
        albumEntity2.setId(2L);
        albumEntity2.setName("album 2 name");
        albumsEntities.add(albumEntity);
        albumsEntities.add(albumEntity2);
        Type listType = new TypeToken<List<AlbumResponseModel>>(){}.getType();
        List<AlbumResponseModel> albumsModels = new ModelMapper().map(albumsEntities, listType);
        return albumsModels;
    }
}
