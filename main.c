
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Constants for the system
#define MAX_FLIGHTS 10
#define MAX_BUSINESS_SEATS 50
#define MAX_ECONOMY_SEATS 100
#define MAX_NAME_LENGTH 50
#define MAX_DESTINATION_LENGTH 50
#define MAX_RESERVATIONS 500

// Structure to represent a Flight
typedef struct {
    int flightNumber;
    char destination[MAX_DESTINATION_LENGTH];
    float businessPrice;
    float economyPrice;
    int availableBusinessSeats;
    int availableEconomySeats;
    int businessSeats[MAX_BUSINESS_SEATS]; // 0 for available, 1 for reserved
    int economySeats[MAX_ECONOMY_SEATS];   // 0 for available, 1 for reserved
} Flight;

// Structure to represent a Reservation
typedef struct {
    char passengerName[MAX_NAME_LENGTH];
    int flightNumber;
    char destination[MAX_DESTINATION_LENGTH];
    int seatNumber;
    int classType; // 1 for Business, 2 for Economy
    float price;
} Reservation;

// Global variables
Flight flights[MAX_FLIGHTS];
Reservation reservations[MAX_RESERVATIONS];
int *flightCount = NULL;
int *reservationCount = NULL;

// Function prototypes
void initializeSystem();
void displayMainMenu();
void displayAdminMenu();
void displayCustomerMenu(char *customerName);
void addFlight();
void displaySeatAvailability();
void displayAllReservations();
void displayTicketPrices();
void searchFlightsByDestination(char *customerName);
void reserveSeat(char *customerName);
void cancelReservation(char *customerName);
void checkTotalAmountSpent(char *customerName);
int findFlightByNumber(int flightNumber);
int findFirstAvailableSeat(int flightIndex, int classType);

// Main function
int main() {
    // Initialize the system
    initializeSystem();
    
    int mainChoice;
    char customerName[MAX_NAME_LENGTH];
    
    do {
        // Display main menu and get user choice
        printf("Select role (1 for Admin, 2 for Customer, 0 to Exit): ");
        scanf("%d", &mainChoice);
        
        switch (mainChoice) {
            case 1: // Admin
                displayAdminMenu();
                break;
            case 2: // Customer
                printf("Enter your name: ");
                scanf(" %[^\n]", customerName); // Read name with spaces
                displayCustomerMenu(customerName);
                break;
            case 0: // Exit
                printf("Exiting the system. Goodbye!\n");
                break;
            default:
                printf("Invalid role. Please select again.\n");
        }
    } while (mainChoice != 0);
    
    // Free allocated memory before exiting
    free(flightCount);
    free(reservationCount);
    
    return 0;
}

// Initialize the system
void initializeSystem() {
    // Allocate memory for the count variables
    flightCount = (int *)malloc(sizeof(int));
    reservationCount = (int *)malloc(sizeof(int));
    
    // Initialize counts to zero
    *flightCount = 0;
    *reservationCount = 0;
    
    // Initialize all flight seats as available (0)
    for (int i = 0; i < MAX_FLIGHTS; i++) {
        for (int j = 0; j < MAX_BUSINESS_SEATS; j++) {
            flights[i].businessSeats[j] = 0;
        }
        for (int j = 0; j < MAX_ECONOMY_SEATS; j++) {
            flights[i].economySeats[j] = 0;
        }
    }
}

// Display the admin menu and handle admin operations
void displayAdminMenu() {
    int adminChoice;
    
    do {
        printf("\nAdmin Menu:\n");
        printf("1. Add a flight\n");
        printf("2. Display seat availability\n");
        printf("3. Display all reservations\n");
        printf("4. Display ticket prices for each flight\n");
        printf("5. Exit to main menu\n");
        printf("Enter your choice: ");
        scanf("%d", &adminChoice);
        
        switch (adminChoice) {
            case 1:
                addFlight();
                break;
            case 2:
                displaySeatAvailability();
                break;
            case 3:
                displayAllReservations();
                break;
            case 4:
                displayTicketPrices();
                break;
            case 5:
                printf("Returning to main menu...\n");
                break;
            default:
                printf("Invalid choice! Please try again.\n");
        }
    } while (adminChoice != 5);
}

// Display the customer menu and handle customer operations
void displayCustomerMenu(char *customerName) {
    int customerChoice;
    
    do {
        printf("\nCustomer Menu:\n");
        printf("1. Search for flights by destination\n");
        printf("2. Reserve a seat\n");
        printf("3. Cancel a reservation\n");
        printf("4. Check total amount spent\n");
        printf("5. Exit to main menu\n");
        printf("Enter your choice: ");
        scanf("%d", &customerChoice);
        
        switch (customerChoice) {
            case 1:
                searchFlightsByDestination(customerName);
                break;
            case 2:
                reserveSeat(customerName);
                break;
            case 3:
                cancelReservation(customerName);
                break;
            case 4:
                checkTotalAmountSpent(customerName);
                break;
            case 5:
                printf("Returning to main menu...\n");
                break;
            default:
                printf("Invalid choice! Please try again.\n");
        }
    } while (customerChoice != 5);
}

// Add a new flight (Admin function)
void addFlight() {
    if (*flightCount >= MAX_FLIGHTS) {
        printf("No more flights can be added.\n");
        return;
    }
    
    Flight newFlight;
    
    // Input flight details
    printf("Enter Flight Number: ");
    scanf("%d", &newFlight.flightNumber);
    
    // Check if flight number already exists
    for (int i = 0; i < *flightCount; i++) {
        if (flights[i].flightNumber == newFlight.flightNumber) {
            printf("Flight number already exists. Please use a different number.\n");
            return;
        }
    }
    
    printf("Enter Destination: ");
    scanf(" %[^\n]", newFlight.destination);
    
    printf("Enter Business Class Ticket Price: ");
    scanf("%f", &newFlight.businessPrice);
    
    printf("Enter Economy Class Ticket Price: ");
    scanf("%f", &newFlight.economyPrice);
    
    // Initialize available seats
    newFlight.availableBusinessSeats = MAX_BUSINESS_SEATS;
    newFlight.availableEconomySeats = MAX_ECONOMY_SEATS;
    
    // Initialize all seats as available (0)
    for (int i = 0; i < MAX_BUSINESS_SEATS; i++) {
        newFlight.businessSeats[i] = 0;
    }
    for (int i = 0; i < MAX_ECONOMY_SEATS; i++) {
        newFlight.economySeats[i] = 0;
    }
    
    // Add the new flight to the array
    flights[*flightCount] = newFlight;
    (*flightCount)++;
    
    printf("Flight added successfully.\n");
}

// Display seat availability for all flights (Admin function)
void displaySeatAvailability() {
    if (*flightCount == 0) {
        printf("No flights to display.\n");
        return;
    }
    
    printf("\nFlight Number  Destination      Business Seats  Economy Seats\n");
    for (int i = 0; i < *flightCount; i++) {
        printf("%-14d %-16s %-15d %-13d\n", 
               flights[i].flightNumber, 
               flights[i].destination, 
               flights[i].availableBusinessSeats, 
               flights[i].availableEconomySeats);
    }
}

// Display all reservations (Admin function)
void displayAllReservations() {
    if (*reservationCount == 0) {
        printf("No reservations to display.\n");
        return;
    }
    
    printf("\nPassenger Name       Flight Number  Destination      Seat Number  1=Business, 2=Economy\n");
    for (int i = 0; i < *reservationCount; i++) {
        printf("%-20s %-14d %-16s %-12d %-5d\n", 
               reservations[i].passengerName, 
               reservations[i].flightNumber, 
               reservations[i].destination, 
               reservations[i].seatNumber, 
               reservations[i].classType);
    }
}

// Display ticket prices for each flight (Admin function)
void displayTicketPrices() {
    if (*flightCount == 0) {
        printf("No flights to display ticket prices.\n");
        return;
    }
    
    printf("\nFlight Number  Destination      Business Price  Economy Price\n");
    for (int i = 0; i < *flightCount; i++) {
        printf("%-14d %-16s %-15.2f %-13.2f\n", 
               flights[i].flightNumber, 
               flights[i].destination, 
               flights[i].businessPrice, 
               flights[i].economyPrice);
    }
}

// Search for flights by destination (Customer function)
void searchFlightsByDestination(char *customerName) {
    if (*flightCount == 0) {
        printf("No flights available.\n");
        return;
    }
    
    char destination[MAX_DESTINATION_LENGTH];
    printf("Enter destination: ");
    scanf(" %[^\n]", destination);
    
    int found = 0;
    for (int i = 0; i < *flightCount; i++) {
        if (strcasecmp(flights[i].destination, destination) == 0) {
            printf("Flight Number: %d, Available Business Seats: %d, Available Economy Seats: %d\n", 
                   flights[i].flightNumber, 
                   flights[i].availableBusinessSeats, 
                   flights[i].availableEconomySeats);
            found = 1;
        }
    }
    
    if (!found) {
        printf("No flights found for the destination %s.\n", destination);
    }
}

// Reserve a seat on a flight (Customer function)
void reserveSeat(char *customerName) {
    if (*flightCount == 0) {
        printf("No flights available.\n");
        return;
    }
    
    // Get destination from customer
    char destination[MAX_DESTINATION_LENGTH];
    printf("Enter destination: ");
    scanf(" %[^\n]", destination);
    
    // Find flights to the destination
    int found = 0;
    int matchingFlights[MAX_FLIGHTS];
    int matchCount = 0;
    
    for (int i = 0; i < *flightCount; i++) {
        if (strcasecmp(flights[i].destination, destination) == 0) {
            printf("Flight Number: %d, Available Business Seats: %d, Available Economy Seats: %d\n", 
                   flights[i].flightNumber, 
                   flights[i].availableBusinessSeats, 
                   flights[i].availableEconomySeats);
            matchingFlights[matchCount++] = i;
            found = 1;
        }
    }
    
    if (!found) {
        printf("No flights found for destination %s.\n", destination);
        return;
    }
    
    // Get class type preference
    int classType;
    printf("Select class (1 for Business, 2 for Economy): ");
    scanf("%d", &classType);
    
    if (classType != 1 && classType != 2) {
        printf("Invalid class type selected.\n");
        return;
    }
    
    // Use the first matching flight (as per requirements)
    int flightIndex = matchingFlights[0];
    
    // Check seat availability
    if (classType == 1 && flights[flightIndex].availableBusinessSeats == 0) {
        printf("No available seats in Business class.\n");
        return;
    } else if (classType == 2 && flights[flightIndex].availableEconomySeats == 0) {
        printf("No available seats in Economy class.\n");
        return;
    }
    
    // Find first available seat
    int seatNumber = findFirstAvailableSeat(flightIndex, classType);
    
    if (seatNumber == -1) {
        printf("Error finding an available seat.\n");
        return;
    }
    
    // Reserve the seat
    if (classType == 1) {
        flights[flightIndex].businessSeats[seatNumber] = 1;
        flights[flightIndex].availableBusinessSeats--;
    } else {
        flights[flightIndex].economySeats[seatNumber] = 1;
        flights[flightIndex].availableEconomySeats--;
    }
    
    // Create a reservation
    Reservation newReservation;
    strcpy(newReservation.passengerName, customerName);
    newReservation.flightNumber = flights[flightIndex].flightNumber;
    strcpy(newReservation.destination, flights[flightIndex].destination);
    newReservation.seatNumber = seatNumber;
    newReservation.classType = classType;
    newReservation.price = (classType == 1) ? flights[flightIndex].businessPrice : flights[flightIndex].economyPrice;
    
    // Add the reservation to the array
    reservations[*reservationCount] = newReservation;
    (*reservationCount)++;
    
    printf("Reservation successful! Flight %d, Seat Number: %d, Class: %s\n", 
           flights[flightIndex].flightNumber, 
           seatNumber, 
           (classType == 1) ? "Business" : "Economy");
}

// Cancel a reservation (Customer function)
void cancelReservation(char *customerName) {
    if (*reservationCount == 0) {
        printf("No reservations to cancel.\n");
        return;
    }
    
    int flightNumber, seatNumber, classType;
    
    printf("Enter Flight Number: ");
    scanf("%d", &flightNumber);
    
    int flightIndex = findFlightByNumber(flightNumber);
    if (flightIndex == -1) {
        printf("Flight %d does not exist.\n", flightNumber);
        return;
    }
    
    printf("Enter Seat Number: ");
    scanf("%d", &seatNumber);
    
    printf("Enter Class Type (Business: 1, Economy: 2): ");
    scanf("%d", &classType);
    
    if (classType != 1 && classType != 2) {
        printf("Invalid class type selected.\n");
        return;
    }
    
    // Check if the seat is reserved
    if ((classType == 1 && seatNumber >= MAX_BUSINESS_SEATS) || 
        (classType == 2 && seatNumber >= MAX_ECONOMY_SEATS)) {
        printf("Invalid seat number.\n");
        return;
    }
    
    if ((classType == 1 && flights[flightIndex].businessSeats[seatNumber] == 0) || 
        (classType == 2 && flights[flightIndex].economySeats[seatNumber] == 0)) {
        printf("Seat %d in Flight %d is not reserved. Nothing to be canceled.\n", 
               seatNumber, flightNumber);
        return;
    }
    
    // Find the reservation
    int reservationIndex = -1;
    for (int i = 0; i < *reservationCount; i++) {
        if (reservations[i].flightNumber == flightNumber && 
            reservations[i].seatNumber == seatNumber && 
            reservations[i].classType == classType && 
            strcmp(reservations[i].passengerName, customerName) == 0) {
            reservationIndex = i;
            break;
        }
    }
    
    if (reservationIndex == -1) {
        printf("No reservation found for this flight, seat, and class type.\n");
        return;
    }
    
    // Mark the seat as available
    if (classType == 1) {
        flights[flightIndex].businessSeats[seatNumber] = 0;
        flights[flightIndex].availableBusinessSeats++;
    } else {
        flights[flightIndex].economySeats[seatNumber] = 0;
        flights[flightIndex].availableEconomySeats++;
    }
    
    // Remove the reservation (shift all elements after it one position to the left)
    for (int i = reservationIndex; i < *reservationCount - 1; i++) {
        reservations[i] = reservations[i + 1];
    }
    (*reservationCount)--;
    
    printf("Reservation for Flight %d and Seat %d has been canceled.\n", flightNumber, seatNumber);
}

// Check total amount spent by a customer (Customer function)
void checkTotalAmountSpent(char *customerName) {
    float totalAmount = 0.0;
    
    for (int i = 0; i < *reservationCount; i++) {
        if (strcmp(reservations[i].passengerName, customerName) == 0) {
            totalAmount += reservations[i].price;
        }
    }
    
    printf("Total amount spent by %s: %.2f\n", customerName, totalAmount);
}

// Find a flight by its flight number
int findFlightByNumber(int flightNumber) {
    for (int i = 0; i < *flightCount; i++) {
        if (flights[i].flightNumber == flightNumber) {
            return i;
        }
    }
    return -1; // Flight not found
}

// Find the first available seat in a given class for a flight
int findFirstAvailableSeat(int flightIndex, int classType) {
    if (classType == 1) { // Business class
        for (int i = 0; i < MAX_BUSINESS_SEATS; i++) {
            if (flights[flightIndex].businessSeats[i] == 0) {
                return i;
            }
        }
    } else if (classType == 2) { // Economy class
        for (int i = 0; i < MAX_ECONOMY_SEATS; i++) {
            if (flights[flightIndex].economySeats[i] == 0) {
                return i;
            }
        }
    }
    return -1; // No available seat found
}
