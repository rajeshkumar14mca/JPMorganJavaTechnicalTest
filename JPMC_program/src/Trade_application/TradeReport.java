package Trade_application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

class Trade {
	private String entity;
    private String buySellFlag; 
    private double agreedFx;
    private String currency;
    private Date instructionDate;
    private Date settlementDate;
    private int units;
    private double pricePerUnit;
    private double usdAmount;

    public Trade(String entity, String buySellFlag, double agreedFx, String currency, String instructionDate, String settlementDate, int units, double pricePerUnit) throws ParseException {
        this.entity = entity;
        this.buySellFlag = buySellFlag;
        this.agreedFx = agreedFx;
        this.currency = currency;
        this.instructionDate = new SimpleDateFormat("dd MMM yyyy").parse(instructionDate);
        this.settlementDate = adjustSettlementDate(new SimpleDateFormat("dd MMM yyyy").parse(settlementDate), currency);
        this.units = units;
        this.pricePerUnit = pricePerUnit;
        this.usdAmount = units * pricePerUnit * agreedFx;
    }

    private Date adjustSettlementDate(Date settlementDate, String currency) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(settlementDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (currency.equals("AED") || currency.equals("SAR")) {
            if (dayOfWeek == Calendar.FRIDAY) {
                calendar.add(Calendar.DATE, 2); 
            } else if (dayOfWeek == Calendar.SATURDAY) {
                calendar.add(Calendar.DATE, 1); 
            }
        } else { 
            if (dayOfWeek == Calendar.SATURDAY) {
                calendar.add(Calendar.DATE, 2); 
            } else if (dayOfWeek == Calendar.SUNDAY) {
                calendar.add(Calendar.DATE, 1); 
            }
        }
        return calendar.getTime();
    }

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getBuySellFlag() {
		return buySellFlag;
	}

	public void setBuySellFlag(String buySellFlag) {
		this.buySellFlag = buySellFlag;
	}

	public double getAgreedFx() {
		return agreedFx;
	}

	public void setAgreedFx(double agreedFx) {
		this.agreedFx = agreedFx;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getInstructionDate() {
		return instructionDate;
	}

	public void setInstructionDate(Date instructionDate) {
		this.instructionDate = instructionDate;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public double getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(double pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public double getUsdAmount() {
		return usdAmount;
	}

	public void setUsdAmount(double usdAmount) {
		this.usdAmount = usdAmount;
	}
    
    
}

public class TradeReport {

    public static void main(String[] args) throws ParseException {
        List<Trade> tradeList = new ArrayList<>();
        tradeList.add(new Trade("foo", "B", 0.50, "SGP", "01 Jan 2016", "02 Jan 2016", 200, 100.25));
        tradeList.add(new Trade("bar", "S", 0.22, "AED", "05 Jan 2016", "07 Jan 2016", 450, 150.5));
        tradeList.add(new Trade("baz", "B", 0.40, "USD", "04 Jan 2016", "06 Jan 2016", 150, 75.0));
        tradeList.add(new Trade("qux", "S", 0.35, "SAR", "08 Jan 2016", "10 Jan 2016", 250, 80.0));
        tradeList.add(new Trade("corge", "B", 0.25, "USD", "03 Jan 2016", "05 Jan 2016", 170, 150.0));
        tradeList.add(new Trade("waldo", "S", 0.41, "USD", "02 Jan 2016", "04 Jan 2016", 190, 30.0));

        generateReport(tradeList);
    }
    
    private static void generateReport(List<Trade> tradeList) {
        Map<Date, Double> incomingByDate = tradeList.stream()
                .filter(t -> t.getBuySellFlag().equals("S"))
                .collect(Collectors.groupingBy(t -> t.getSettlementDate(), Collectors.summingDouble(t -> t.getUsdAmount())));
        
        System.out.println("Amount in USD settled incoming by Date:");
        incomingByDate.forEach((date, amount) -> System.out.println(new SimpleDateFormat("dd MMM yyyy").format(date) + ": $" + amount));

        Map<Date, Double> outgoingByDate = tradeList.stream()
                .filter(t -> t.getBuySellFlag().equals("B"))
                .collect(Collectors.groupingBy(t -> t.getSettlementDate(), Collectors.summingDouble(t -> t.getUsdAmount())));

        System.out.println("\nAmount in USD settled outgoing by Date:");
        outgoingByDate.forEach((date, amount) -> System.out.println(new SimpleDateFormat("dd MMM yyyy").format(date) + ": $" + amount));
        
        Map<String, Double> outgoingRanking = tradeList.stream()
                .filter(t -> t.getBuySellFlag().equals("B"))
                .collect(Collectors.groupingBy(t -> t.getEntity(), Collectors.summingDouble(t -> t.getUsdAmount())));
        
        System.out.println("\nEntity Outgoing Ranking (USD):");
        outgoingRanking.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(e -> System.out.println(e.getKey() + ": $" + e.getValue()));
        
        Map<String, Double> incomingRanking = tradeList.stream()
                .filter(t -> t.getBuySellFlag().equals("S"))
                .collect(Collectors.groupingBy(t -> t.getEntity(), Collectors.summingDouble(t -> t.getUsdAmount())));


        System.out.println("\nEntity Incoming Ranking (USD):");
        incomingRanking.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(e -> System.out.println(e.getKey() + ": $" + e.getValue()));
    }
}
