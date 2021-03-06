/*
 * Copyright 2020 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.v2d.geometry.envelope;

import ch.obermuhlner.math.big.BigRational;
import java.math.BigDecimal;
import java.util.Objects;
import uk.ac.leeds.ccg.v2d.core.V2D_Environment;
import uk.ac.leeds.ccg.v2d.geometry.V2D_FiniteGeometry;
import uk.ac.leeds.ccg.v2d.geometry.V2D_Geometry;
import uk.ac.leeds.ccg.v2d.geometry.V2D_Line;
import uk.ac.leeds.ccg.v2d.geometry.V2D_LineSegment;
import uk.ac.leeds.ccg.v2d.geometry.V2D_Point;

/**
 * An envelope contains all the extreme values with respect to the X and Y axes.
 * It is an axis aligned bounding box, which may have length of zero in any
 * direction. For a point the envelope is essentially the point.
 *
 * @author Andy Turner
 * @version 1.0
 */
public class V2D_Envelope extends V2D_Geometry implements V2D_FiniteGeometry {

    private static final long serialVersionUID = 1L;

    /**
     * The minimum x-coordinate.
     */
    private BigRational xMin;

    /**
     * The maximum x-coordinate.
     */
    private BigRational xMax;

    /**
     * The minimum y-coordinate.
     */
    private BigRational yMin;

    /**
     * The maximum y-coordinate.
     */
    private BigRational yMax;

    /**
     * The top edge.
     */
    protected V2D_EnvelopeEdgeTop t;

    /**
     * The right edge.
     */
    protected V2D_EnvelopeEdgeRight r;

    /**
     * The bottom edge.
     */
    protected V2D_EnvelopeEdgeBottom b;

    /**
     * The left edge.
     */
    protected V2D_EnvelopeEdgeLeft l;

    /**
     * @param e An envelop.
     */
    public V2D_Envelope(V2D_Envelope e) {
        yMin = e.yMin;
        yMax = e.yMax;
        xMin = e.xMin;
        xMax = e.xMax;
        init();
    }

    private void init() {
        V2D_Point tl = new V2D_Point(getxMin(), getyMax());
        V2D_Point tr = new V2D_Point(getxMax(), getyMax());
        V2D_Point bl = new V2D_Point(getxMax(), getyMin());
        V2D_Point br = new V2D_Point(getxMin(), getyMin());
        t = new V2D_EnvelopeEdgeTop(tl, tr);
        r = new V2D_EnvelopeEdgeRight(tr, br);
        b = new V2D_EnvelopeEdgeBottom(br, bl);
        l = new V2D_EnvelopeEdgeLeft(bl, tl);
    }

    /**
     * @param points The points used to form the envelop.
     */
    public V2D_Envelope(V2D_Point... points) {
        if (points.length > 0) {
            xMin = points[0].x;
            xMax = points[0].x;
            yMin = points[0].y;
            yMax = points[0].y;
            for (int i = 1; i < points.length; i++) {
                xMin = BigRational.min(xMin, points[i].x);
                xMax = BigRational.max(xMax, points[i].x);
                yMin = BigRational.min(yMin, points[i].y);
                yMax = BigRational.max(yMax, points[i].y);
            }
            init();
        }
    }

    /**
     * @param x The x-coordinate of a point.
     * @param y The y-coordinate of a point.
     */
    public V2D_Envelope(BigRational x, BigRational y) {
        xMin = x;
        xMax = x;
        yMin = y;
        yMax = y;
        init();
    }

    /**
     * @param x The x-coordinate of a point.
     * @param y The y-coordinate of a point.
     */
    public V2D_Envelope(BigDecimal x, BigDecimal y) {
        xMin = BigRational.valueOf(x);
        xMax = BigRational.valueOf(x);
        yMin = BigRational.valueOf(y);
        yMax = BigRational.valueOf(y);
        init();
    }
    
    /**
     * @param xMin What {@link xMin} is set to.
     * @param xMax What {@link xMax} is set to.
     * @param yMin What {@link yMin} is set to.
     * @param yMax What {@link yMax} is set to.
     */
    public V2D_Envelope(BigRational xMin, BigRational xMax,
            BigRational yMin, BigRational yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        init();
    }

    /**
     * @param xMin What {@link xMin} is set to.
     * @param xMax What {@link xMax} is set to.
     * @param yMin What {@link yMin} is set to.
     * @param yMax What {@link yMax} is set to.
     */
    public V2D_Envelope(BigDecimal xMin, BigDecimal xMax,
            BigDecimal yMin, BigDecimal yMax) {
        this.xMin = BigRational.valueOf(xMin);
        this.xMax = BigRational.valueOf(xMax);
        this.yMin = BigRational.valueOf(yMin);
        this.yMax = BigRational.valueOf(yMax);
        init();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "(xMin=" + getxMin().toString() + ", xMax=" + getxMax().toString()
                + ", yMin=" + getyMin().toString() + ", yMax=" + getyMax().toString() + ")";
    }

    /**
     * @param e The V2D_Envelope to union with this.
     * @return an Envelope which is {@code this} union {@code e}.
     */
    public V2D_Envelope union(V2D_Envelope e) {
        if (e.isContainedBy(this)) {
            return this;
        } else {
            return new V2D_Envelope(BigRational.min(e.getxMin(), getxMin()),
                    BigRational.max(e.getxMax(), getxMax()),
                    BigRational.min(e.getyMin(), getyMin()),
                    BigRational.max(e.getyMax(), getyMax()));
        }
    }

    /**
     * If {@code e} touches, or overlaps then it intersects.
     *
     * @param e The Vector_Envelope2D to test for intersection.
     * @return {@code true} if this intersects with {@code e}.
     */
    public boolean isIntersectedBy(V2D_Envelope e) {
        // Does this contain any corners of e?
        boolean re = isIntersectedBy(e.getxMin(), e.getyMin());
        if (re) {
            return re;
        }
        re = isIntersectedBy(e.getxMin(), e.getyMax());
        if (re) {
            return re;
        }
        re = isIntersectedBy(e.getxMax(), e.getyMin());
        if (re) {
            return re;
        }
        re = isIntersectedBy(e.getxMax(), e.getyMax());
        if (re) {
            return re;
        }
        // Does e contain any corners of this
        re = e.isIntersectedBy(getxMax(), getyMax());
        if (re) {
            return re;
        }
        re = e.isIntersectedBy(getxMin(), getyMax());
        if (re) {
            return re;
        }
        re = e.isIntersectedBy(getxMax(), getyMin());
        if (re) {
            return re;
        }
        re = e.isIntersectedBy(getxMin(), getyMin());
        if (re) {
            return re;
        }
        /**
         * Check to see if xMin and xMax are between e.xMin and e.xMax, e.yMin
         * and e.yMax are between yMin and yMax, and e.zMin and e.zMax are
         * between zMin and zMax.
         */
        if (e.getxMax().compareTo(getxMax()) != 1 && e.getxMax().compareTo(getxMin()) != -1
                && e.getxMin().compareTo(getxMax()) != 1
                && e.getxMin().compareTo(getxMin()) != -1) {
            if (getyMin().compareTo(e.getyMax()) != 1 && getyMin().compareTo(e.getyMin()) != -1
                    && getyMax().compareTo(e.getyMax()) != 1
                    && getyMax().compareTo(e.getyMin()) != -1) {
                    return true;
            }
        }
        /**
         * Check to see if e.xMin and e.xMax are between xMax, yMin and yMax are
         * between e.yMin and e.yMax, and zMin and zMax are between e.zMin and
         * e.zMax.
         */
        if (getxMax().compareTo(e.getxMax()) != 1 && getxMax().compareTo(e.getxMin()) != -1
                && getxMin().compareTo(e.getxMax()) != 1
                && getxMin().compareTo(e.getxMin()) != -1) {
            if (e.getyMin().compareTo(getyMax()) != 1 && e.getyMin().compareTo(getyMin()) != -1
                    && e.getyMax().compareTo(getyMax()) != 1
                    && e.getyMax().compareTo(getyMin()) != -1) {
                    return true;
            }
        }
        return false;
    }

    /**
     * Containment includes the boundary. So anything in or on the boundary is
     * contained.
     *
     * @param e V2D_Envelope
     * @return if this is contained by {@code e}
     */
    public boolean isContainedBy(V2D_Envelope e) {
        return this.getxMax().compareTo(e.getxMax()) != 1
                && this.getxMin().compareTo(e.getxMin()) != -1
                && this.getyMax().compareTo(e.getyMax()) != 1
                && this.getyMin().compareTo(e.getyMin()) != -1;
    }

//    /**
//     * @param l Line segment to intersect with {@code this}.
//     * @param flag For distinguishing between this method and
//     * {@link #getIntersection(uk.ac.leeds.ccg.v2d.geometry.V2D_Line)}.
//     * @return either a point or line segment which is the intersection of
//     * {@code l} and {@code this}.
//     */
//    public V2D_Geometry getIntersection(V2D_LineSegment l, boolean flag) {
//        V2D_Envelope le = l.getEnvelope();
//        if (le.isIntersectedBy(this)) {
//            V2D_Envelope ei = le.getIntersection(this);
//            return ei.getIntersection(l);
//        }
//        return null;
//    }

    /**
     * @param p The point to test for intersection.
     * @return {@code true} if this intersects with {@code p}
     */
    public boolean isIntersectedBy(V2D_Point p) {
        return isIntersectedBy(p.x, p.y);
    }

    /**
     * @param x The x-coordinate of the point to test for intersection.
     * @param y The y-coordinate of the point to test for intersection.
     * @return {@code true} if this intersects with {@code p}
     */
    public boolean isIntersectedBy(BigRational x, BigRational y) {
        return x.compareTo(getxMin()) != -1 && x.compareTo(getxMax()) != 1
                && y.compareTo(getyMin()) != -1 && y.compareTo(getyMax()) != 1;
    }

    /**
     * @param l The V2D_LineSegment to test for intersection.
     * @return {@code true} if this intersects with {@code p}
     */
    public boolean isIntersectedBy(V2D_LineSegment l) {
        if (this.isIntersectedBy(l.p) || this.isIntersectedBy(l.q)) {
            return true;
        } else {
            if (l.isIntersectedBy(new V2D_EnvelopeEdgeTop(this))){
                return true;
            }
            if (l.isIntersectedBy(new V2D_EnvelopeEdgeBottom(this))){
                return true;
            }
            if (l.isIntersectedBy(new V2D_EnvelopeEdgeLeft(this))){
                return true;
            }
            if (l.isIntersectedBy(new V2D_EnvelopeEdgeRight(this))){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param en The envelop to intersect.
     * @return {@code null} if there is no intersection; {@code en} if
     * {@code this.equals(en)}; otherwise returns the intersection.
     */
    public V2D_Envelope getIntersection(V2D_Envelope en) {
        if (this.equals(en)) {
            return en;
        }
        if (!this.isIntersectedBy(en)) {
            return null;
        }
        return new V2D_Envelope(BigRational.max(getxMin(), en.getxMin()),
                BigRational.min(getxMax(), en.getxMax()), 
                BigRational.max(getyMin(), en.getyMin()),
                BigRational.min(getyMax(), en.getyMax()));
    }

//    /**
//     * Returns {@code null} if {@code this} does not intersect {@code l};
//     * otherwise returns the intersection which is either a point or a line
//     * segment.
//     *
//     * @param li The line to intersect.
//     * @return {@code null} if there is no intersection; otherwise returns the
//     * intersection.
//     */
//    public V2D_Geometry getIntersection(V2D_Line li) {
//        V2D_Geometry tli = t.getIntersection(li);
//        if (tli == null) {
//            // Check l, a, r, f, b
//            V2D_Geometry lli = l.getIntersection(li);
//            if (lli == null) {
//                // Check a, r, f, b
//                V2D_Geometry ali = a.getIntersection(li);
//                if (ali == null) {
//                    // Check r, f, b
//                    V2D_Geometry rli = r.getIntersection(li);
//                    if (rli == null) {
//                        // Check f, b
//                        V2D_Geometry fli = f.getIntersection(li);
//                        if (fli == null) {
//                            // null intersection.
//                            return null;
//                        } else if (fli instanceof V2D_LineSegment) {
//                            return fli;
//                        } else {
//                            V2D_Point flip = (V2D_Point) fli;
//                            V2D_Point blip = (V2D_Point) b.getIntersection(li);
//                            if (flip.equals(blip)) {
//                                return blip;
//                            } else {
//                                return new V2D_LineSegment(flip, blip);
//                            }
//                        }
//                    } else if (rli instanceof V2D_LineSegment) {
//                        return rli;
//                    } else {
//                        V2D_Point rlip = (V2D_Point) rli;
//                        // check for intersection with b
//                        V2D_Geometry bli = b.getIntersection(li);
//                        if (bli == null) {
//                            return rlip;
//                        } else {
//                            return new V2D_LineSegment((V2D_Point) bli, rlip);
//                        }
//                    }
//                } else if (ali instanceof V2D_LineSegment) {
//                    return ali;
//                } else {
//                    // Check for intersection with r, f, b
//                    V2D_Point alip = (V2D_Point) ali;
//                    V2D_Geometry rli = r.getIntersection(li);
//                    if (rli == null) {
//                        // Check f, b
//                        V2D_Geometry fli = f.getIntersection(li);
//                        if (fli == null) {
//                            // check for intersection with b
//                            V2D_Geometry bli = b.getIntersection(li);
//                            if (bli == null) {
//                                return alip;
//                            } else if (bli instanceof V2D_LineSegment) {
//                                return bli;
//                            } else {
//                                return new V2D_LineSegment((V2D_Point) bli, alip);
//                            }
//                        } else if (fli instanceof V2D_LineSegment) {
//                            return fli;
//                        } else {
//                            return new V2D_LineSegment((V2D_Point) fli, alip);
//                        }
//                    } else {
//                        return new V2D_LineSegment((V2D_Point) rli, alip);
//                    }
//                }
//            }
//        } else if (tli instanceof V2D_LineSegment) {
//            return tli;
//        } else {
//            V2D_Point tlip = (V2D_Point) tli;
//            // Check l, a, r, f, b
//            V2D_Geometry lli = l.getIntersection(li);
//            if (lli == null) {
//                // Check a, r, f, b
//                V2D_Geometry ali = a.getIntersection(li);
//                if (ali == null) {
//                    // Check r, f, b
//                    V2D_Geometry rli = r.getIntersection(li);
//                    if (rli == null) {
//                        // Check f, b
//                        V2D_Geometry fli = f.getIntersection(li);
//                        if (fli == null) {
//                            // Intersects b
//                            V2D_Point blip = (V2D_Point) b.getIntersection(li);
//                            return new V2D_LineSegment(tlip, blip);
//                        } else if (fli instanceof V2D_LineSegment) {
//                            return fli;
//                        } else {
//                            // Could have a corner so still need to check b
//                            // so far here there is an intersection with t and f
//                            // and there is no intersection with a, r, and l
//                            // check for intersection with b
//                            V2D_Geometry bli = b.getIntersection(li);
//                            if (bli == null) {
//                                V2D_Point blip = (V2D_Point) bli;
//                                if (tlip.equals(blip)) {
//                                    return tlip;
//                                } else {
//                                    return new V2D_LineSegment(tlip, blip);
//                                }
//                            } else if (bli instanceof V2D_LineSegment) {
//                                return bli;
//                            } else {
//                                return new V2D_LineSegment((V2D_Point) bli, tlip);
//                            }
//                        }
//                    } else if (rli instanceof V2D_LineSegment) {
//                        return rli;
//                    } else {
//                        V2D_Point rlip = (V2D_Point) rli;
//                        // check for intersection with b
//                        V2D_Geometry bli = b.getIntersection(li);
//                        if (bli == null) {
//                            return rlip;
//                        } else {
//                            return new V2D_LineSegment((V2D_Point) bli, rlip);
//                        }
//                    }
//                } else if (ali instanceof V2D_LineSegment) {
//                    return ali;
//                } else {
//                    // Check for intersection with r, f, b
//                    V2D_Point alip = (V2D_Point) ali;
//                    V2D_Geometry rli = r.getIntersection(li);
//                    if (rli == null) {
//                        // Check f, b
//                        V2D_Geometry fli = f.getIntersection(li);
//                        if (fli == null) {
//                            // check for intersection with b
//                            V2D_Geometry bli = b.getIntersection(li);
//                            if (bli == null) {
//                                return alip;
//                            } else if (bli instanceof V2D_LineSegment) {
//                                return bli;
//                            } else {
//                                return new V2D_LineSegment((V2D_Point) bli, alip);
//                            }
//                        } else if (fli instanceof V2D_LineSegment) {
//                            return fli;
//                        } else {
//                            return new V2D_LineSegment((V2D_Point) fli, alip);
//                        }
//                    } else {
//                        V2D_Point rlip = (V2D_Point) rli;
//                        if (rlip.equals(alip)) {
//                            // Still more checking to do...
//                            // Check f, b
//                            V2D_Geometry fli = f.getIntersection(li);
//                            if (fli == null) {
//                                // check for intersection with b
//                                V2D_Geometry bli = b.getIntersection(li);
//                                if (bli == null) {
//                                    return alip;
//                                } else if (bli instanceof V2D_LineSegment) {
//                                    return bli;
//                                } else {
//                                    return new V2D_LineSegment((V2D_Point) bli, alip);
//                                }
//                            } else if (fli instanceof V2D_LineSegment) {
//                                return fli;
//                            } else {
//                                return new V2D_LineSegment((V2D_Point) fli, alip);
//                            }
//                        } else {
//                            return new V2D_LineSegment((V2D_Point) rli, alip);
//                        }
//                    }
//                }
//            } else if (lli instanceof V2D_LineSegment) {
//                return lli;
//            } else {
//                // Still more checking to do...
//                // intersection top and left could be at a corner and anyway need to check other edges...
//                V2D_Point llip = (V2D_Point) lli;
//                if (tlip.equals(llip)) {
//                    return tlip;
//                } else {
//                    return new V2D_LineSegment(tlip, llip);
//                }
//            }
//        }
//        return null; // Should not get here remove after writing test cases.
//    }

    @Override
    public V2D_Envelope getEnvelope() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof V2D_Envelope) {
            V2D_Envelope en = (V2D_Envelope) o;
            if (this.getxMin().compareTo(en.getxMin()) == 0
                    && this.getxMax().compareTo(en.getxMax()) == 0
                    && this.getyMin().compareTo(en.getyMin()) == 0
                    && this.getyMax().compareTo(en.getyMax()) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.getxMin());
        hash = 43 * hash + Objects.hashCode(this.getxMax());
        hash = 43 * hash + Objects.hashCode(this.getyMin());
        hash = 43 * hash + Objects.hashCode(this.getyMax());
        return hash;
    }

    /**
     * @return the xMin
     */
    public BigRational getxMin() {
        return xMin;
    }

    /**
     * @return the xMax
     */
    public BigRational getxMax() {
        return xMax;
    }

    /**
     * @return the yMin
     */
    public BigRational getyMin() {
        return yMin;
    }

    /**
     * @return the yMax
     */
    public BigRational getyMax() {
        return yMax;
    }

}
