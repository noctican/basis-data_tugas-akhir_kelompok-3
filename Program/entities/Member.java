package entities;

import java.math.BigDecimal;

public class Member {
    private String jenis;
    private int poin;
    private BigDecimal voucherPrice;
    private BigDecimal voucherPercentage;

    public Member() {}

    public Member(String jenis, int poin, BigDecimal voucherPrice, BigDecimal voucherPercentage) {
        this.jenis = jenis;
        this.poin = poin;
        this.voucherPrice = voucherPrice;
        this.voucherPercentage = voucherPercentage;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public int getPoin() {
        return poin;
    }

    public void setPoin(int poin) {
        this.poin = poin;
    }

    public BigDecimal getVoucherPrice() {
        return voucherPrice;
    }

    public void setVoucherPrice(BigDecimal voucherPrice) {
        this.voucherPrice = voucherPrice;
    }

    public BigDecimal getVoucherPercentage() {
        return voucherPercentage;
    }

    public void setVoucherPercentage(BigDecimal voucherPercentage) {
        this.voucherPercentage = voucherPercentage;
    }

    @Override
    public String toString() {
        return jenis;
    }
}
