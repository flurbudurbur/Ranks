package dev.flur.ranks.service;

/**
 * Interface for services that have a lifecycle.
 */
public interface Lifecycle {

    /**
     * Starts the service.
     * This method should be called when the service is first created.
     */
    void start();

    /**
     * Stops the service.
     * This method should be called when the service is no longer needed.
     */
    void stop();

    /**
     * Checks if the service is healthy.
     *
     * @return True if the service is healthy, false otherwise
     */
    boolean isHealthy();
}