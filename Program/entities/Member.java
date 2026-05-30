package entities;

import java.math.BigDecimal;

public class Member {
    private String jenis;
    private int poin;
    private BigDecimal voucherPrice;
    private BigDecimal voucherPercentage;

    public Member() {}

    public String getJenis() { return jenis; }
    public void setJenis(String j) { this.jenis = j; }
    public int getPoin() { return poin; }
    public void setPoin(int p) { this.poin = p; }
    public BigDecimal getVoucherPrice() { return voucherPrice; }
    public void setVoucherPrice(BigDecimal v) { this.voucherPrice = v; }
    public BigDecimal getVoucherPercentage() { return voucherPercentage; }
    public void setVoucherPercentage(BigDecimal v) { this.voucherPercentage = v; }

    @Override
    public String toString() { return jenis; }
}
