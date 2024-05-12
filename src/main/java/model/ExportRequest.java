package model;

import java.util.List;

public class ExportRequest {
    private List<String> columnsToExport;
    private boolean csvExport;
    private boolean txtExport;
    public List<String> getColumnsToExport() {
        return columnsToExport;
    }
    public boolean isCsvExport() {
        return csvExport;
    }
    public boolean isTxtExport() {
        return txtExport;
    }
    @Override
    public String toString() {
        return "ExportRequest{" +
                "columnsToExport=" + columnsToExport +
                ", csvExport=" + csvExport +
                ", txtExport=" + txtExport +
                '}';
    }
}
