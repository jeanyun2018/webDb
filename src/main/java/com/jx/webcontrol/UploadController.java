package com.jx.webcontrol;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jx.inter.IEventBusManage;
import com.jx.webconfig.ParameterBridge;

/**
 * 上传文件类
 * 
 * @author jean
 *
 */
@Controller
public class UploadController {
	@Value("${jx.filePathLocation}")
	private String UPLOADED_FOLDER;
	private String filePath;
	@Autowired
	private IEventBusManage eventBusManage;

	@RequestMapping("/toUpload")
	public String home() {
		return "upload";
	}

	@PostMapping("/upload")
	public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:uploadStatus";
		}

		try {
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
			filePath = UPLOADED_FOLDER + file.getOriginalFilename();
			Files.write(path, bytes);

			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded '" + file.getOriginalFilename() + "'");

		} catch (IOException e) {
			e.printStackTrace();
		}
		/* 通过参数桥，设置传输文件路径参数 */
		ParameterBridge pb = new ParameterBridge();
		pb.setFilePath(filePath);
		eventBusManage.postDbBus(pb);
		/* 重定页面，防止刷新重复上传 */
		return "redirect:uploadStatus";
	}

	@GetMapping("/uploadStatus")
	public String uploadStatus() {
		return "uploadStatus";
	}
}
