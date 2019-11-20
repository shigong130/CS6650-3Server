package com.neu.manager;

import com.neu.dao.LiftDao;
import com.neu.dao.RideDao;
import com.neu.pojo.Lift;
import com.neu.pojo.Ride;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SkierManager {
    private static final int THRESHOLD = 1000;

    private static SkierManager skierManager = null;


    RideDao rideDao = new RideDao();
    LiftDao liftDao = new LiftDao();
    //Lazy initialization;
    Map<Integer, Integer> verticalMap;
    BlockingQueue<Ride> queue;

    public SkierManager(BlockingQueue<Ride> queue) {
        rideDao = new RideDao();
        liftDao = new LiftDao();
        this.queue = queue;

    }


    public boolean addRide(int skierId, int resortId, int season, int day, int liftId, int time) {
        Ride r = new Ride();
        r.setTime(time);
        r.setLiftId(liftId);
        r.setDay(day);
        r.setSeason(season);
        r.setResortId(resortId);
        r.setSkierId(skierId);

        boolean result = rideDao.insertRide(r);
        return result;
    }

    public boolean batchAddRide(int skierId, int resortId, int season, int day, int liftId, int time){
        Ride r = new Ride();
        r.setTime(time);
        r.setLiftId(liftId);
        r.setDay(day);
        r.setSeason(season);
        r.setResortId(resortId);
        r.setSkierId(skierId);


        queue.add(r);

        boolean result = true;

        if(queue.size()==THRESHOLD){
            result = flushRideQueue();
            queue.clear();
        }

        return result;
    }


    public boolean flushRideQueue() {
        boolean result = rideDao.batchInsertRide(queue);
        queue.clear();

        return result;
    }

    public String getVerticalBySkier(int skierId, int resortId) {
        return getVerticalBySkier(skierId, resortId, -1);
    }

    public String getVerticalBySkier(int skierId, int resortId, int season) {
        getVerticalMap();
        int result = 0;
        List<Ride> rideList = rideDao.getRidesBySkierId(skierId, resortId);

        for(Ride r : rideList){
            if(season!=-1 && r.getSeason()!=season){
                continue;
            }

            int liftId = r.getLiftId();
            if(verticalMap.containsKey(liftId)) {
                result += verticalMap.get(liftId);
            }
        }

        if(season!=-1) {
            String jsonTemplate = "{\n" +
                    "  \"resorts\": [\n" +
                    "    {\n" +
                    "      \"seasonID\": \"%d\",\n" +
                    "      \"totalVert\": %d\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            return String.format(jsonTemplate, season, result);
        } else {
            String jsonTemplate = "{\n" +
                    "  \"resorts\": [\n" +
                    "    {\n" +
                    "      \"totalVert\": %d\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            return String.format(jsonTemplate, result);
        }

    }

    public String getVerticalBySkierAndDay(int skierId, int resortId, int season, int day) {
        getVerticalMap();
        int result = 0;
        List<Ride> rideList = rideDao.getRidesBySkierTrip(skierId, resortId, season, day);

        for(Ride r : rideList){
            int liftId = r.getLiftId();

            if(verticalMap.containsKey(liftId)) {
                result += verticalMap.get(liftId);
            }
        }

        return new Integer(result).toString();
    }

    private void getVerticalMap(){
        if (verticalMap != null) return;

        verticalMap = new HashMap<Integer, Integer>();

        List<Lift> liftList = liftDao.getAllLifts();
        if(liftList==null || liftList.size()==0) return;

        for(Lift lift : liftList){
            verticalMap.put(lift.getId(), lift.getVertical());
        }
    }

    public static void main(String[] args){
        BlockingQueue<Ride> queue = new ArrayBlockingQueue<Ride>(100000);
        SkierManager manager = new SkierManager(queue);
        /*
        System.out.println(manager.addRide(300, 2, 2019, 201, 2, 112));
        */


        /*
        System.out.println(
                manager.getVerticalBySkier(300, 2, 2019)
        );
        */

        /*
        System.out.println(
                manager.getVerticalBySkierAndDay(300, 2, 2019, 200)
        );
        */



    }
}
