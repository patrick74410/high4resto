package fr.high4technology.high4resto.bean.Horaire;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Struct.BetweenTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Horaire {
    @Id
    private String id;
    @Getter
    @Builder.Default
    private List<BetweenTime> lundi = new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> mardi = new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> mercredi = new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> jeudi = new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> vendredi = new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> samedi = new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> dimanche = new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> ferie = new ArrayList<BetweenTime>();

    public double[] generatePlanning()
    {
        double result[]=new double[24*60];
        Arrays.fill(result, 1.0);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        List<BetweenTime> currentTime = new ArrayList<BetweenTime>();
        switch(dayOfWeek){
            case 0:
                currentTime.addAll(dimanche);
            break;
            case 1:
                currentTime.addAll(lundi);
            break;
            case 2:
                currentTime.addAll(mardi);
            break;
            case 3:
                currentTime.addAll(mercredi);
            break;
            case 4:
                currentTime.addAll(jeudi);
            break;
            case 5:
                currentTime.addAll(vendredi);
            break;
            case 6:
                currentTime.addAll(samedi);
            break;
        }
        for(BetweenTime bTime:currentTime)
        {
            int begin=Integer.parseInt(bTime.getDebut().split(":")[0])*60+Integer.parseInt(bTime.getDebut().split(":")[1]);
            int end=Integer.parseInt(bTime.getFin().split(":")[0])*60+Integer.parseInt(bTime.getFin().split(":")[1]);
            for(int i=begin;i!=end;i++)
            {
                result[i]=0.0;
            }

        }
        return result;
    }
}
