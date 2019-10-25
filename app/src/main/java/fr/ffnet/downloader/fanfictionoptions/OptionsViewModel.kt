package fr.ffnet.downloader.fanfictionoptions

//    private val _getFile: SingleLiveEvent<String> = SingleLiveEvent()
//    val getFile: SingleLiveEvent<String> get() = _getFile
//
//    fun buildPdf(fanfictionId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            databaseRepository.getCompleteFanfiction(fanfictionId)?.let { fanfiction ->
//                val file = pdfBuilder.buildPdf(fanfiction)
//                _getFile.postValue(file)
//            }
//        }
//    }
//
//    fun buildEpub(fanfictionId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            databaseRepository.getCompleteFanfiction(fanfictionId)?.let { fanfiction ->
//                val fileName = epubBuilder.buildEpub(fanfiction)
//                _getFile.postValue(fileName)
//            }
//        }
//    }