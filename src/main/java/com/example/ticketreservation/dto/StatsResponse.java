package com.example.ticketreservation.dto;

public record StatsResponse(long totalUsers, long totalEvents, long confirmedReservations,
                            long ticketsSold, double revenue) {}
