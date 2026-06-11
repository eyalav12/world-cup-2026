package com.world_cup.demo.service;

import com.opencsv.CSVReader;
import com.world_cup.demo.entities.HistoryMatchData;
import com.world_cup.demo.publisher.RabbitMQProducer;
import com.world_cup.demo.repositories.HistoryMatchDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvLoaderService {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);
    private static HistoryMatchDataRepository historyMatchDataRepository;

    public CsvLoaderService(HistoryMatchDataRepository historyMatchDataRepository){
        this.historyMatchDataRepository = historyMatchDataRepository;
    }

    public static void main(String[] args) {
//        handleFileLoader();
    }

    public void handleFileLoader(){
        try{
            if(!historyMatchDataRepository.findAll().isEmpty()){
                return;
            }
            List<String> headers = new ArrayList<>();
            List<List<String>> rowsData = new ArrayList<>();
            FileReader fileReader=new FileReader( "C:\\Users\\Eyal\\Downloads\\archive (1)\\FIFA World Cup 1930-2022 All Match Dataset.csv");
            CSVReader csvReader=new CSVReader(fileReader);
            String [] nextRecord;
            boolean firstRow = true;
            while((nextRecord=csvReader.readNext())!=null){
                List<String> currentRow = new ArrayList<>();
                for(String cell:nextRecord){
                    if(firstRow){
                        headers.add(cell);
                    }
                    else{
                        currentRow.add(cell);
                    }
                    System.out.print(cell + "\t");
                }
                if(!firstRow){
                    rowsData.add(currentRow);
                }
                firstRow=false;

            }
            String s="";
            List<HistoryMatchData> list = func(rowsData, headers);
            saveToDB(list);
            s="p";
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void saveToDB(List<HistoryMatchData> historyMatchDataList){
        if(historyMatchDataRepository.findAll().size()>0){
            return;
        }
        for(HistoryMatchData historyMatchData:historyMatchDataList){
            HistoryMatchData save = historyMatchDataRepository.save(historyMatchData);
            logger.info("saving "+save.getId());
        }
    }

    private List<HistoryMatchData> func(List<List<String>> rowsData,List<String> headers) throws NoSuchFieldException, IllegalAccessException {
        List<Map<String,String>> list=new ArrayList<>();
        for(List<String> row:rowsData){
            Map<String,String> map=new HashMap<>();
            for(int i=0;i<row.size();i++){
                if(i == 0 || i == 9 || i == 10 || i == 12 || i == 17 || i == 20){
                    continue;
                }
                String key=headers.get(i);
                String first = key.substring(0,1).toLowerCase();
                String rest = key.substring(1);
                rest=rest.replace(" ","");
                String realKey=first+rest;
                map.put(realKey,row.get(i));
            }
            list.add(map);
        }
        List<HistoryMatchData> historyMatchDataList = new ArrayList<>();
        for(Map<String,String> map:list){
            HistoryMatchData historyMatchData = new HistoryMatchData(map);
            historyMatchDataList.add(historyMatchData);
        }
        return historyMatchDataList;
    }

}
