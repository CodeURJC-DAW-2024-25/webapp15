export interface ReviewDTO {
    id: number;                  // Long in Java mapped to number in TypeScript
    date: string;                // LocalDate in Java mapped to a string (yyyy-mm-dd) in TypeScript
    rating: number;              // int in Java mapped to number in TypeScript
    description: string;
    shoeId: number;              // Relating to the shoe (using shoeId to avoid circular reference)
    userId: number;              // Relating to the user who made the review
    userName: string;            // User's name who made the review
  }
  