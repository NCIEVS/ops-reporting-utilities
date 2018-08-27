package gov.nih.nci.evs.wusage.excel;

import gov.nih.nci.evs.wusage.object.BandwidthComparator;
import gov.nih.nci.evs.wusage.object.Calculator;
import gov.nih.nci.evs.wusage.object.EntitySorter;
import gov.nih.nci.evs.wusage.object.HostEntity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


// Possibly write to Excel and insert chart?
// Useful charts/tables
// Visits, Visitors for each browser
// KB per domain
// Users from each domain, sorted by KB
// duration of visits?? - comes from separate area of wusage page
public class ExcelHandler {

	
	// See http://poi.apache.org/spreadsheet/quick-guide.html for instructions
	Workbook wb;
	

	
	public ExcelHandler(Vector<HostEntity> entities, String workbookName){
		wb = new HSSFWorkbook();
		
		Sheet sheet1 = wb.createSheet("sheet1");
//		CreationHelper createHelp = wb.getCreationHelper();
		Iterator<HostEntity> iter = entities.iterator();
		
		//create header row
		Row rowHead = sheet1.createRow(0);
		rowHead.createCell(0).setCellValue("Name");
		rowHead.createCell(1).setCellValue("Host");
		rowHead.createCell(2).setCellValue("Country");
		rowHead.createCell(3).setCellValue("City");
		rowHead.createCell(4).setCellValue("Pages");
		rowHead.createCell(5).setCellValue("Hits");
		rowHead.createCell(6).setCellValue("Bandwidth");
		rowHead.createCell(7).setCellValue("Last visit");
		rowHead.createCell(8).setCellValue("KB");
		rowHead.createCell(9).setCellValue("domain");
		
		int rowNum = 1;
		while(iter.hasNext()){
			Row row = sheet1.createRow(rowNum);
			HostEntity entity = iter.next();
			row.createCell(0).setCellValue(entity.getHostName());
			row.createCell(1).setCellValue(entity.getHost());
			row.createCell(2).setCellValue(entity.getCountry());
			row.createCell(3).setCellValue(entity.getCity());
			row.createCell(4).setCellValue(entity.getPages());
			row.createCell(5).setCellValue(entity.getHits());
			row.createCell(6).setCellValue(entity.getBandwidth_st());
			row.createCell(7).setCellValue(entity.getLastVisit_date());
			row.createCell(8).setCellValue(entity.getBandwidth_dou());
			row.createCell(9).setCellValue(entity.getDomain());
			rowNum++;
		}
		
//		printTopTenHosts(entities);
		printTopTwentyHosts(entities);

		closeWorkbook(workbookName);
	}
	
	public void printTopTenHosts(Vector<HostEntity> entities){
		//TODO print to a worksheet 
//		private void processOrderByBandwidth(Vector<HostEntity> entities){
			HashMap<String,Vector<HostEntity>> byDomain = EntitySorter.groupByDomain(entities);
			Set<String> domainStringSet = byDomain.keySet();
			Iterator<String> byDomainIter = domainStringSet.iterator();
			while(byDomainIter.hasNext()){
				String domainString = byDomainIter.next();
				HashMap<String,Vector<HostEntity>> byHostName = EntitySorter.groupByHostName(byDomain.get(domainString));
				TreeMap<String,Double> tempMap = Calculator.calculateTopTen(byHostName);
				BandwidthComparator comp = new BandwidthComparator(tempMap);
				TreeMap<String,Double> topTen = new TreeMap<String,Double>(comp);
				topTen.putAll(tempMap);
				printTopTenHostPage(topTen, domainString);
			}
			
	}
	
	public void printTopTenHostPage(TreeMap<String,Double> topTen, String domain){
		Sheet domainSheet = wb.createSheet(domain);
		DecimalFormat df = new DecimalFormat("0.00");
		Set<String> hostSet = topTen.keySet();
		Iterator<String> hostIter = hostSet.iterator();
		
		//create header row
		Row rowHead = domainSheet.createRow(0);
		rowHead.createCell(0).setCellValue("Name");
		rowHead.createCell(1).setCellValue("Bandwidth"); 
		
		int i=1;
		
		while(hostIter.hasNext()){
			Row row = domainSheet.createRow(i);
			String host = hostIter.next();
			Double bandwidth = topTen.get(host);
			String formatBandwidth = df.format(bandwidth);
			if(i < 10){
				row.createCell(0).setCellValue(host);
				row.createCell(1).setCellValue(formatBandwidth);
//			System.out.println(host + "  " + formatBandwidth + " MB");
			}
			else{break;}
			i++;
		}
		
	}
	
	public void printTopTwentyHosts(Vector<HostEntity> entities){
		//TODO print to a worksheet 
//		private void processOrderByBandwidth(Vector<HostEntity> entities){
			HashMap<String,Vector<HostEntity>> byDomain = EntitySorter.groupByDomain(entities);
			Set<String> domainStringSet = byDomain.keySet();
			Iterator<String> byDomainIter = domainStringSet.iterator();
			while(byDomainIter.hasNext()){
				String domainString = byDomainIter.next();
				HashMap<String,Vector<HostEntity>> byHostName = EntitySorter.groupByHostName(byDomain.get(domainString));
				TreeMap<String,Double> tempMap = Calculator.calculateTopTen(byHostName);
				BandwidthComparator comp = new BandwidthComparator(tempMap);
				TreeMap<String,Double> topTen = new TreeMap<String,Double>(comp);
				topTen.putAll(tempMap);
				printTopTwentyHostPage(topTen, domainString);
			}
			
	}
	
	public void printTopTwentyHostPage(TreeMap<String,Double> topTen, String domain){
		Sheet domainSheet = wb.createSheet(domain);
		DecimalFormat df = new DecimalFormat("0.00");
		Set<String> hostSet = topTen.keySet();
		Iterator<String> hostIter = hostSet.iterator();
		
		//create header row
		Row rowHead = domainSheet.createRow(0);
		rowHead.createCell(0).setCellValue("Name");
		rowHead.createCell(1).setCellValue("Bandwidth"); 
		
		int i=1;
		
		while(hostIter.hasNext()){
			Row row = domainSheet.createRow(i);
			String host = hostIter.next();
			Double bandwidth = topTen.get(host);
			String formatBandwidth = df.format(bandwidth);
			if(i < 20){
				row.createCell(0).setCellValue(host);
				row.createCell(1).setCellValue(formatBandwidth);
//			System.out.println(host + "  " + formatBandwidth + " MB");
			}
			else{break;}
			i++;
		}
		
	}
	
	private void closeWorkbook(String workbookName){
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(workbookName + ".xls");
			wb.write(fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
