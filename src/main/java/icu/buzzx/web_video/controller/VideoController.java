package icu.buzzx.web_video.controller;

import icu.buzzx.web_video.property.ApplicationProperty;
import icu.buzzx.web_video.util.GeneralUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import static icu.buzzx.web_video.constants.ApplicationConstants.*;

@RestController
@RequestMapping("/video")
public class VideoController {

    private final ApplicationProperty property;
    @Autowired
    public VideoController(ApplicationProperty property) {
        this.property = property;
    }

    @GetMapping("/test/{fileName}")
    public String testAPI(@PathVariable(required = false) String fileName) {
//        return "Hi from springboot";
        long rst = GeneralUtils.getFileSize(property.getVideoPath() + fileName);
        return String.valueOf(rst);
    }

    /**
     * get partial of @param fileName.@param fileType
     * @param fileName video name
     * @param fileType video type
     * @param range range of resource, multiple range is not support currently
     * @return full response: header + body
     */
    @GetMapping(value = "/video_stream/{fileName}/{fileType}")
    public ResponseEntity<byte[]> getVideoByName(
            @PathVariable(value = "fileName") String fileName,
            @PathVariable(value = "fileType", required = false) String fileType,
            @RequestHeader(value = "Range", required = false) String range) {
        if (fileType == null) fileType = TYPE_MP4;
        String fileFullName = property.getVideoPath() + fileName + "." + fileType;
        long fileSize = GeneralUtils.getFileSize(fileFullName);
        if (fileSize < 0) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Video not found".getBytes());
        }

        long rangeStart = 0;
        long rangeEnd = STREAM_BLOCK;

        if (range != null) {
            // range is like bytes=[start]-[end], this substring will trim prefix "bytes="
            range = range.substring(6);
            String[] ranges = range.split(", ");
            // multiple range is not support
            String[] idxs = ranges[0].split("-");
            rangeStart = Long.parseLong(idxs[0]);
            if (rangeStart > fileSize) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Video finished".getBytes());
            }
            if (idxs.length > 1) rangeEnd = Long.parseLong(idxs[1]);
            if (rangeEnd <= rangeStart) rangeEnd = rangeStart + STREAM_BLOCK;
        }

        if (rangeEnd >= fileSize) rangeEnd = fileSize - 1;

        byte[] partialResource = GeneralUtils.readFile(fileFullName, rangeStart, rangeEnd);

        if (partialResource == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Video not found".getBytes());
        }

        HttpStatus status = HttpStatus.PARTIAL_CONTENT;
        if (rangeEnd == fileSize - 1) status = HttpStatus.OK;
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.valueOf(VIDEO_TYPE + fileType));
        header.set(HttpHeaders.ACCEPT_RANGES, BYTES);
        if (range == null) header.setContentLength(fileSize);
        else header.setContentLength(rangeEnd - rangeStart);
        header.set(HttpHeaders.CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize);

        return ResponseEntity
                .status(status)
                .headers(header)
                .body(partialResource);
    }
}
