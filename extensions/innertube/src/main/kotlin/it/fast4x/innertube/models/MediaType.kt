package it.fast4x.innertube.models

sealed class MediaType {
    data object Song : MediaType()

    data object Video : MediaType()
}