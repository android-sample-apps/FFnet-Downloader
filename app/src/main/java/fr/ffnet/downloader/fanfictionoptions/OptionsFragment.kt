package fr.ffnet.downloader.fanfictionoptions

//    companion object {
//        private const val STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
//        private const val EXPORT_EPUB_REQUEST = 2000
//        private const val EXPORT_PDF_REQUEST = 2001
//    }
//
//        viewModel.getFile.observe(this, Observer { fileName ->
//            val file = File(
//                Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOWNLOADS
//                ), fileName
//            )
//            // Open file with user selected app
//            val uri = Uri.fromFile(file).normalizeScheme()
//            val mimeValue = getMimeType(uri.toString())
//            val intent = Intent().apply {
//                action = Intent.ACTION_VIEW
//                data = uri
//                type = mimeValue
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//            requireContext().startActivity(Intent.createChooser(intent, "Open file with"))
//        })
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            when (requestCode) {
//                EXPORT_EPUB_REQUEST -> exportEpub()
//                EXPORT_PDF_REQUEST -> exportPdf()
//            }
//        } else {
//            AlertDialog
//                .Builder(context)
//                .setTitle(R.string.export_permission_title)
//                .setMessage(R.string.export_permission_content)
//                .setPositiveButton(R.string.export_permission_grant) { _, _ ->
//                    checkPermission(requestCode)
//                }
//                .setNegativeButton(R.string.export_permission_deny) { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .create()
//                .show()
//        }
//    }
//
//    private fun checkPermission(requestCode: Int): Boolean {
//        return if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestPermissions(arrayOf(STORAGE), requestCode)
//            false
//        } else true
//    }
//
//    private fun exportEpub() {
//        if (checkPermission(EXPORT_EPUB_REQUEST)) {
//            viewModel.buildEpub(fanfictionId)
//        }
//    }
//
//    private fun exportPdf() {
//        if (checkPermission(EXPORT_PDF_REQUEST)) {
//            viewModel.buildPdf(fanfictionId)
//        }
//    }
//
//    private fun getMimeType(url: String): String? {
//        val ext = MimeTypeMap.getFileExtensionFromUrl(url)
//        var mime: String? = null
//        if (ext != null) {
//            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
//        }
//        return mime
//    }
//}
