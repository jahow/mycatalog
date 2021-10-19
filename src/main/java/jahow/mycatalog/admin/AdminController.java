package jahow.mycatalog.admin;

import jahow.mycatalog.api.CatalogRecord;
import jahow.mycatalog.api.CatalogRecordsService;
import jahow.mycatalog.api.OperationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${openapi.myCatalog.base-path}/admin")
public class AdminController {
  @Autowired
  CatalogRecordsService recordsService;

  @GetMapping({"/", "index"})
  public String showRecordsList(Model model) {
    var result = this.recordsService.getCatalogRecords();
    model.addAttribute("records", result.hasSucceeded() ? result.getResultValue() : null);
    return "records-list";
  }

  @GetMapping("")
  public String redirectToIndex(Model model) {
    return "redirect:./admin/index";
  }

  @GetMapping("/record/{identifier}")
  public String showRecord(@PathVariable("identifier") String recordIdentifier, Model model) {
    var view = this.preRecordView(recordIdentifier, model);
    return view != null ? view : "record-view";
  }

  @GetMapping("/record/{identifier}/edit")
  public String editRecord(@PathVariable("identifier") String recordIdentifier, Model model) {
    var view = this.preRecordView(recordIdentifier, model);
    model.addAttribute("isNew", false);
    return view != null ? view : "record-edit";
  }

  @GetMapping("/record/new")
  public String editNewRecord(Model model) {
    model.addAttribute("record", new CatalogRecord());
    model.addAttribute("isNew", true);
    return "record-edit";
  }

  @PostMapping("/record")
  public String saveRecord(@ModelAttribute("record") CatalogRecord record, Model model) {
    var readResult = this.recordsService.readCatalogRecord(record.getIdentifier());
    OperationResult<CatalogRecord> upsertResult;
    if (readResult.hasSucceeded()) {
      upsertResult = this.recordsService.updateCatalogRecord(record);
    } else {
      upsertResult = this.recordsService.createCatalogRecord(record);
    }
    if (!upsertResult.hasSucceeded()) {
      return "record-edit";
    }
    var upsertedRecord = upsertResult.getResultValue();
    return String.format("redirect:./%s", upsertedRecord.getIdentifier());
  }

  private String preRecordView(String recordIdentifier, Model model) {
    var result = this.recordsService.readCatalogRecord(recordIdentifier);
    if (!result.hasSucceeded()) {
      return "record-invalid";
    }
    model.addAttribute("record", result.getResultValue());
    return null;
  }
}
