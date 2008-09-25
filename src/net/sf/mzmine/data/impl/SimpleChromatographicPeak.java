/*
 * Copyright 2006-2008 The MZmine Development Team
 * 
 * This file is part of MZmine.
 * 
 * MZmine is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.data.impl;

import java.util.Arrays;
import java.util.Iterator;

import net.sf.mzmine.data.ChromatographicPeak;
import net.sf.mzmine.data.DataPoint;
import net.sf.mzmine.data.MzPeak;
import net.sf.mzmine.data.PeakStatus;
import net.sf.mzmine.data.RawDataFile;
import net.sf.mzmine.util.PeakUtils;
import net.sf.mzmine.util.Range;

/**
 * This class is a simple implementation of the peak interface.
 */
public class SimpleChromatographicPeak implements ChromatographicPeak {

    private PeakStatus peakStatus;
    private RawDataFile dataFile;

    private int scanNumbers[];
    private MzPeak mzPeaksPerScan[];

    // M/Z, RT, Height and Area
    private float mz, rt, height, area;

    // Boundaries of the peak
    private Range rtRange, mzRange, intensityRange;

    /**
     * Initializes a new peak using given values
     * 
     */
    public SimpleChromatographicPeak(RawDataFile dataFile, float MZ, float RT, float height,
            float area, int[] scanNumbers, MzPeak[] mzPeaksPerScan,
            PeakStatus peakStatus) {

        if (mzPeaksPerScan.length == 0) {
            throw new IllegalArgumentException(
                    "Cannot create a SimplePeak instance with no data points");
        }

        this.dataFile = dataFile;

        this.mz = MZ;
        this.rt = RT;
        this.height = height;
        this.area = area;

        this.scanNumbers = scanNumbers;
        this.mzPeaksPerScan = mzPeaksPerScan;
        

        for (int ind = 0; ind < scanNumbers.length; ind++) {

            float dataPointRT = dataFile.getScan(scanNumbers[ind]).getRetentionTime();

            // Update RT range
            if (ind == 0) {
                rtRange = new Range(dataPointRT);
            } else {
                rtRange.extendRange(dataPointRT);
            }

            // Update m/z and intensity ranges
            for (DataPoint dp : mzPeaksPerScan[ind].getRawDataPoints()) {

                if (ind == 0) {
                    mzRange = new Range(dp.getMZ());
                    intensityRange = new Range(dp.getIntensity());
                } else {
                    mzRange.extendRange(dp.getMZ());
                    intensityRange.extendRange(dp.getIntensity());
                }

            }

        }

        this.peakStatus = peakStatus;

    }

    /**
     * Copy constructor
     */
    public SimpleChromatographicPeak(ChromatographicPeak p) {

        this.dataFile = p.getDataFile();

        this.mz = p.getMZ();
        this.rt = p.getRT();
        this.height = p.getHeight();
        this.area = p.getArea();

        this.rtRange = p.getRawDataPointsRTRange();
        this.mzRange = p.getRawDataPointsMZRange();
        this.intensityRange = p.getRawDataPointsIntensityRange();

        this.scanNumbers = p.getScanNumbers();
        this.mzPeaksPerScan = new MzPeak[scanNumbers.length];

        for (int i = 0; i < scanNumbers.length; i++) {
            mzPeaksPerScan[i] = p.getMzPeak(scanNumbers[i]);
        }

        this.peakStatus = p.getPeakStatus();

    }

    /**
     * This method returns the status of the peak
     */
    public PeakStatus getPeakStatus() {
        return peakStatus;
    }

    /**
     * This method returns M/Z value of the peak
     */
    public float getMZ() {
        return mz;
    }

    public void setMZ(float mz) {
        this.mz = mz;
    }

    public void setRT(float rt) {
        this.rt = rt;
    }

    /**
     * This method returns retention time of the peak
     */
    public float getRT() {
        return rt;
    }

    /**
     * This method returns the raw height of the peak
     */
    public float getHeight() {
        return height;
    }

    /**
     * @param height The height to set.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * This method returns the raw area of the peak
     */
    public float getArea() {
        return area;
    }

    /**
     * @param area The area to set.
     */
    public void setArea(float area) {
        this.area = area;
    }

    /**
     * This method returns numbers of scans that contain this peak
     */
    public int[] getScanNumbers() {
        return scanNumbers;
    }

    /**
     * This method returns a representative datapoint of this peak in a given
     * scan
     */
    public MzPeak getMzPeak(int scanNumber) {
        int index = Arrays.binarySearch(scanNumbers, scanNumber);
        if (index < 0)
            return null;
        return mzPeaksPerScan[index];
    }


    /**
     * @see net.sf.mzmine.data.ChromatographicPeak#getDataFile()
     */
    public RawDataFile getDataFile() {
        return dataFile;
    }

    /**
     * @see net.sf.mzmine.data.ChromatographicPeak#setDataFile()
     */
    public void setDataFile(RawDataFile dataFile) {
        this.dataFile = dataFile;
    }

    public String toString() {
        return PeakUtils.peakToString(this);
    }

    public Range getRawDataPointsIntensityRange() {
        return intensityRange;
    }

    public Range getRawDataPointsMZRange() {
        return mzRange;
    }

    public Range getRawDataPointsRTRange() {
        return rtRange;
    }

	public int getRepresentativeScanNumber() {
		Range range = new Range(rt);
		return dataFile.getScanNumbers(1, range)[0];
	}

}
